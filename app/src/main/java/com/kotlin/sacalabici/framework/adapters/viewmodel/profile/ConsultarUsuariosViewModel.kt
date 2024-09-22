package com.kotlin.sacalabici.framework.adapters.viewmodel

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

    fun getUsuarios() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarios = consultarUsuariosRepository.getUsuarios(limit = 0)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        _usuarios.value = usuarios!!
                    } else {
                        _errorMessage.value = "La lista de usuarios está vacía o es nula."
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "No se pudieron obtener los usuarios: ${e.message}"
            }
        }
    }

    fun searchUser(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarios = consultarUsuariosRepository.searchUser(username) // Pass the actual username
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        _usuarios.value = usuarios!!
                    } else {
                        _errorMessage.value = "No users found for this username."
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error fetching users: ${e.message}"
            }
        }
    }
}
