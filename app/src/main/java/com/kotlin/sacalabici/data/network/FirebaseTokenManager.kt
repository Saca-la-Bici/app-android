package com.kotlin.sacalabici.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseTokenManager(private val firebaseAuth: FirebaseAuth) {

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    fun getIdToken() {
        firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _token.value = task.result?.token
            } else {
                Log.e("token", "Error al obtener el token", task.exception)
                _token.value = null
            }
        }
    }

    suspend fun getTokenSynchronously(): String? {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    _token.value = token
                    continuation.resume(token)
                } else {
                    Log.e("token", "Error al obtener el token", task.exception)
                    continuation.resume(null)
                }
            }
        }
    }
}

