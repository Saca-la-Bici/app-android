/**
 * File: ConsultarUsuariosViewModel.kt
 * Description: Esta clase gestiona la consulta de usuarios desde una base de datos, permitiendo la
 *              paginación y la búsqueda de usuarios por nombre y rol. Utiliza Firebase para la
 *              autenticación y obtiene el token necesario para realizar las consultas.
 * Date: 16/10/2024
 * Changes:
 */

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
    private val pageSize = 12
    private var currentRoles: String = "Administrador,Usuario"
    private var isLastPage = false
    var scrollPosition = 0

    /**
     * Guarda la posición del scroll en la variable `scrollPosition`.
     * @param position La nueva posición del scroll a guardar.
     */
    fun updateScrollPosition(position: Int) {
        scrollPosition = position
    }

    // Bandera para saber si estamos en modo búsqueda
    var isSearching = false
    private var firebaseAuth: FirebaseAuth

    init {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /**
     * Obtiene usuarios desde la base de datos con paginación.
     * Si se encuentra en modo búsqueda o ya se han cargado todos los usuarios, no realiza la consulta.
     * Se reinicia la paginación si el parámetro `reset` es verdadero.
     * @param roles Roles opcionales para filtrar los usuarios.
     * @param reset Si es verdadero, reinicia la paginación y la lista de usuarios.
     */
    fun getUsuarios(
        roles: String? = null,
        reset: Boolean = false,
    ) {
        // Si ya estamos buscando, no cargamos usuarios paginados
        if (_isLoading.value == true || isLastPage == true || isSearching == true) return

        val rolesToUse = roles ?: currentRoles

        // Reiniciar la paginación si es necesario
        if (reset) {
            currentPage = 1
            _usuarios.value = emptyList() // Reiniciar la lista paginada
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
                            currentList.addAll(usuarios) // Agregar nuevos usuarios a la lista actual
                            _usuarios.value = currentList
                            currentPage++ // Incrementar la página
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

    /**
     * Busca usuarios por nombre y roles opcionales.
     * Cambia el estado a búsqueda y realiza una consulta a la base de datos.
     * @param username Nombre del usuario a buscar.
     * @param roles Roles opcionales para filtrar la búsqueda.
     */
    fun searchUser(
        username: String,
        roles: String? = null,
    ) {
        _isLoading.value = true
        isSearching = true // Cambiamos el estado a búsqueda

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idToken = getFirebaseIdToken(firebaseAuth)
                if (idToken != null) {
                    val buscarUsuariosRequirement = BuscarUsuariosRequirement(idToken)

                    // Modificar la llamada para incluir los roles si existen
                    val usuarios = buscarUsuariosRequirement(username, roles)

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

    /**
     * Obtiene el token de identificación de Firebase para el usuario actual.
     * @param firebaseAuth La instancia de FirebaseAuth utilizada para obtener el token.
     * @return El token de identificación de Firebase o null si ocurre un error.
     */
    private suspend fun getFirebaseIdToken(firebaseAuth: FirebaseAuth): String? =
        try {
            firebaseAuth.currentUser
                ?.getIdToken(true)
                ?.await()
                ?.token
        } catch (e: Exception) {
            null
        }
}
