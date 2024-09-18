package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.AuthState
import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.domain.SessionRequirement
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(email: String, password: String, confirmPassword: String, username: String, edad: Int) {
        if (arePasswordsValid(password, confirmPassword)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            val user = User(
                                username = username,
                                nombre = "",
                                edad = edad,
                                tipoSangre = "",
                                correoElectronico = currentUser.email ?: "",
                                numeroEmergencia = currentUser.phoneNumber ?: "",
                                firebaseUID = currentUser.uid ?: ""
                            )
                            registerUser(user)
                        } else {
                            _authState.postValue(AuthState.Success(firebaseAuth.currentUser))
                        }
                    } else {
                        _authState.postValue(AuthState.Error("Hubo un error al registrar el usuario: ${task.exception?.message}"))
                    }
                }
        } else {
            _authState.postValue(AuthState.Error("Las contraseñas no coinciden o son demasiado cortas"))
        }
    }

    private fun arePasswordsValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6
    }

    private fun registerUser(user: User) {
        viewModelScope.launch {
            val idToken = getFirebaseIdToken()

            if (idToken != null) {
                val sessionRequirement = SessionRequirement(idToken)
                val result = sessionRequirement(user)

                if (result != null) {
                    _authState.postValue(AuthState.Success(firebaseAuth.currentUser))
                } else {
                    firebaseAuth.signOut()
                    _authState.postValue(AuthState.Error("Error al iniciar sesión"))
                }
            } else {
                firebaseAuth.signOut()
                _authState.postValue(AuthState.Error("Error al obtener el token de Firebase"))
            }
        }
    }

    private suspend fun getFirebaseIdToken(): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
}
