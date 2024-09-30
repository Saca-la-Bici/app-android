package com.kotlin.sacalabici.framework.adapters.viewmodel

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

    // Variables para la paginación
    private var currentPage = 1
    private val pageSize = 7
    private var isLoading = false

    private lateinit var firebaseAuth: FirebaseAuth

    fun getUsuarios(reset: Boolean = false) {
        if (isLoading) return  // Evitar hacer la consulta si ya estamos cargando

        if (reset) {
            currentPage = 1  // Reiniciar la paginación
            _usuarios.value = emptyList()  // Limpiar la lista actual de usuarios
        }

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idToken = getFirebaseIdToken(firebaseAuth)
                if (idToken != null) {
                    val consultarUsuariosRequirement = ConsultarUsuariosRequirement(idToken)
                    val usuarios = consultarUsuariosRequirement(page = currentPage, limit = pageSize)
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
                e.printStackTrace()
                _errorMessage.value = "No se pudieron obtener los usuarios: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun searchUser(username: String) {
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
                        }
                    }
                } else {
                    _errorMessage.value = "No se pudo obtener el token de Firebase."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al buscar usuarios: ${e.message}"
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

