package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterContinueViewModel : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(email: String, password: String, confirmPassword: String) {
        if (arePasswordsValid(password, confirmPassword)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _registrationState.postValue(RegistrationState.Success(firebaseAuth.currentUser))
                    } else {
                        _registrationState.postValue(RegistrationState.Error("Account creation failed: ${task.exception?.message}"))
                    }
                }
        } else {
            _registrationState.postValue(RegistrationState.Error("Passwords do not match or are too short"))
        }
    }

    private fun arePasswordsValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6
    }
}

sealed class RegistrationState {
    data class Success(val user: FirebaseUser?) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
