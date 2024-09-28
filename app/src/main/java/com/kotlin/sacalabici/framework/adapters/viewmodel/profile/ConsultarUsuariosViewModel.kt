package com.kotlin.sacalabici.framework.adapters.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsultarUsuariosViewModel : ViewModel() {
    private val _usuarios = MutableLiveData<List<ConsultarUsuariosBase>>()
    val usuarios: LiveData<List<ConsultarUsuariosBase>> get() = _usuarios

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val consultarUsuariosRepository = ConsultarUsuariosRepository()

    // Variables para la paginación
    private var currentPage = 1
    private val pageSize = 7
    private var currentRoles: String = "Administrador,Usuario"
    private var isLastPage = false

    // Bandera para saber si estamos en modo búsqueda
    private var isSearching = false

    // Función para obtener usuarios con paginación
    fun getUsuarios(roles: String? = null, firebaseUID: String, reset: Boolean = false) {
        if (firebaseUID.isBlank()) {
            _errorMessage.value = "Firebase UID no puede estar vacío."
            return
        }

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
                val usuarios = consultarUsuariosRepository.getUsuarios(page = currentPage, limit = pageSize, roles = rolesToUse, firebaseUID = firebaseUID)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        val currentList = _usuarios.value?.toMutableList() ?: mutableListOf()
                        currentList.addAll(usuarios)
                        _usuarios.value = currentList
                        currentPage++
                    } else {
                        isLastPage = true
                        _errorMessage.value = "No hay más usuarios para cargar."
                    }
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
    fun searchUser(username: String, firebaseUID: String) {
        if (firebaseUID.isBlank()) {
            _errorMessage.value = "Firebase UID no puede estar vacío."
            return
        }

        _isLoading.value = true
        isSearching = true  // Cambiamos el estado a búsqueda

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarios = consultarUsuariosRepository.searchUser(username, firebaseUID)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        _usuarios.value = usuarios
                    } else {
                        _errorMessage.value = "No se encontraron usuarios con ese nombre."
                        isSearching = false  // Restaurar si no hay resultados
                    }
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

    // Función para limpiar el estado de búsqueda y volver a la paginación
    fun resetSearch() {
        isSearching = false  // Volver al modo de paginación
        getUsuarios(firebaseUID = "firebaseUID_actual", reset = true)  // Restablecer la paginación si es necesario
    }
}

