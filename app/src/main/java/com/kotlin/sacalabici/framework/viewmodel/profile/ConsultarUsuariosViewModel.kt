package com.kotlin.sacalabici.framework.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.domain.profile.BuscarUsuariosRequirement
import com.kotlin.sacalabici.domain.profile.ConsultarUsuariosRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConsultarUsuariosViewModel : ViewModel() {
    private val _usuarios = MutableLiveData<List<ConsultarUsuariosBase>>()
    val usuarios: LiveData<List<ConsultarUsuariosBase>> get() = _usuarios

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Variables para la paginación
    private var currentPage = 1
    private val pageSize = 7
    private var currentRoles: String = "Administrador,Usuario"
    private var isLastPage = false
    var scrollPosition = 0

    // Función para guardar la posición del scroll
    fun updateScrollPosition(position: Int) {
        scrollPosition = position
    }

    // Bandera para saber si estamos en modo búsqueda
    private var isSearching = false
    private var firebaseAuth: FirebaseAuth

    init {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // Función para obtener usuarios con paginación
    fun getUsuarios(roles: String? = null, reset: Boolean = false) {

        val rolesToUse = roles ?: currentRoles

        // Si ya estamos buscando, no cargamos usuarios paginados
        if (_isLoading.value == true || isLastPage || isSearching) return

        // Reiniciar la paginación si es necesario
        if (reset) {
            currentPage = 1
            _usuarios.value = emptyList()  // Reiniciar la lista paginada
            isLastPage = false
            if (roles != null) {
                currentRoles = roles
            }
        }

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idToken = getFirebaseIdToken(firebaseAuth)
                if (idToken != null) {
                    val consultarUsuariosRequirement = ConsultarUsuariosRequirement(idToken)
                    val usuarios = consultarUsuariosRequirement(page = currentPage, limit = pageSize, roles = rolesToUse)
                    withContext(Dispatchers.Main) {
                        if (!usuarios.isNullOrEmpty()) {
                            val currentList = _usuarios.value?.toMutableList() ?: mutableListOf()
                            currentList.addAll(usuarios)  // Agregar nuevos usuarios a la lista actual
                            _usuarios.value = currentList
                            currentPage++  // Incrementar la página
                        } else {
                            _errorMessage.value = "No hay más usuarios para cargar."
                        }
                    }
                } else {
                    _errorMessage.value = "No se pudo obtener el token de Firebase."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "No se pudieron obtener los usuarios: ${e.message}"
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Función para buscar usuarios
    fun searchUser(username: String) {

        _isLoading.value = true
        isSearching = true  // Cambiamos el estado a búsqueda

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idToken = getFirebaseIdToken(firebaseAuth)
                if (idToken != null) {
                    val buscarUsuariosRequirement = BuscarUsuariosRequirement(idToken)
                    val usuarios = buscarUsuariosRequirement(username)
                    withContext(Dispatchers.Main) {
                        if (!usuarios.isNullOrEmpty()) {
                            _usuarios.value = usuarios!!
                        } else {
                            _errorMessage.value = "No se encontraron usuarios con ese nombre."
                            isSearching = false
                        }
                    }
                } else {
                    _errorMessage.value = "No se pudo obtener el token de Firebase."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error al buscar usuarios: ${e.message}"
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun getFirebaseIdToken(firebaseAuth: FirebaseAuth): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
}

