package com.kotlin.sacalabici.framework.adapters.viewmodel.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(email: String, password: String, confirmPassword: String, username: String, fechaNacimiento: String) {
        val passwordError = arePasswordsValid(password, confirmPassword)
        if (passwordError.isNullOrEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            registerUser(currentUser, username, fechaNacimiento)
                        } else {
                            _authState.postValue(AuthState.Success(firebaseAuth.currentUser))
                        }
                    } else {
                        _authState.postValue(AuthState.Error("Hubo un error al registrar el usuario"))
                    }
                }
        } else {
            _authState.postValue(AuthState.Error(passwordError))
        }
    }

    private fun arePasswordsValid(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) return "Las contraseñas no coinciden"
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!password.contains(Regex("[0-9]"))) return "La contraseña debe contener al menos un número"
        if (!password.contains(Regex("[A-Z]"))) return "La contraseña debe contener al menos una letra mayúscula"
        if (!password.contains(Regex("[a-z]"))) return "La contraseña debe contener al menos una letra minúscula"
        if (!password.contains(Regex("[^a-zA-Z0-9\\s]"))) return "La contraseña debe contener al menos un carácter especial"
        return null // No errors
    }

    private fun registerUser(currentUser: FirebaseUser, username: String, fechaNacimiento: String) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState, username, fechaNacimiento)
        }
    }
}
