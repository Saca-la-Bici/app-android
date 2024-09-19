package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.data.models.AuthState
import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.data.models.UserClient
import com.kotlin.sacalabici.domain.SessionRequirement
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(email: String, password: String, confirmPassword: String, username: String, fechaNacimiento: String) {
        if (arePasswordsValid(password, confirmPassword)) {
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

    private fun registerUser(currentUser: FirebaseUser, username: String, fechaNacimiento: String) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState, username, fechaNacimiento)
        }
    }
}
