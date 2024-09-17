package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.AuthState
import com.kotlin.sacalabici.data.models.RegistrationState
import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.domain.SessionRequirement
import kotlinx.coroutines.launch

class RegisterContinueViewModel : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    private lateinit var firebaseAuth: FirebaseAuth
    private val sessionRequirement = SessionRequirement()

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
                            _registrationState.postValue(RegistrationState.Success(firebaseAuth.currentUser))
                        }
                    } else {
                        _registrationState.postValue(RegistrationState.Error("Hubo un error al registrar el usuario: ${task.exception?.message}"))
                    }
                }
        } else {
            _registrationState.postValue(RegistrationState.Error("Las contraseñas no coinciden o son demasiado cortas"))
        }
    }

    private fun arePasswordsValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6
    }

    private fun registerUser(user: User) {
        viewModelScope.launch {
            try {
                val result = sessionRequirement(user)
                if (result != null) {
                    _registrationState.postValue(RegistrationState.Success(firebaseAuth.currentUser))
                } else {
                    _registrationState.postValue(RegistrationState.Error("Error al registrar usuario"))
                }
            } catch (e: Exception) {
                _registrationState.postValue(RegistrationState.Error("Error: ${e.message}"))
            }
        }
    }
}
