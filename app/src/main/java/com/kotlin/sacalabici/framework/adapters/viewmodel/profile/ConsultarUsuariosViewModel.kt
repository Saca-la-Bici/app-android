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

    private val consultarUsuariosRepository = ConsultarUsuariosRepository()

    // Variables para la paginación
    private var currentPage = 1
    private val pageSize = 7
    private var isLoading = false
    private var currentRoles: String = "Administrador,Usuario" //Valor inicial al entrar a modificar roles

    fun getUsuarios(roles: String? = null, firebaseUID: String, reset: Boolean = false) {
        // Usa los roles actuales si roles es null
        val rolesToUse = roles ?: currentRoles

        Log.d("ConsultarUsuariosViewModel", "Roles a consultar: $rolesToUse")

        // Evitar múltiples llamadas si ya estamos cargando
        if (isLoading) return

        // Reiniciar la paginación y la lista de usuarios si es necesario
        if (reset) {
            currentPage = 1  // Reiniciar la paginación
            _usuarios.value = emptyList()  // Limpiar la lista actual de usuarios
            // Solo actualiza los roles si es necesario
            if (roles != null) {
                currentRoles = roles // Actualiza solo si roles no es null
            }
        }

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarios = consultarUsuariosRepository.getUsuarios(page = currentPage, limit = pageSize, roles = rolesToUse, firebaseUID = firebaseUID)
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
                val usuarios = consultarUsuariosRepository.searchUser(username)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        _usuarios.value = usuarios!!
                    } else {
                        _errorMessage.value = "No se encontraron usuarios con ese nombre."
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al buscar usuarios: ${e.message}"
            }
        }
    }
}

