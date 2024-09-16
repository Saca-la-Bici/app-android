package com.kotlin.sacalabici.data.models

import com.google.firebase.auth.FirebaseUser

sealed class RegistrationState {
    data class Success(val user: FirebaseUser?) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
