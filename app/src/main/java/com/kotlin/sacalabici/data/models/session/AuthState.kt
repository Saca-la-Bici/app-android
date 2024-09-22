package com.kotlin.sacalabici.data.models.session

import com.google.firebase.auth.FirebaseUser

sealed class         AuthState {
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
    object Cancel : AuthState()
    object SignedOut : AuthState() // Add this line
}