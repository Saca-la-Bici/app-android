package com.kotlin.sacalabici.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


// Clase que gestiona la obtención y almacenamiento del token de Firebase
class FirebaseTokenManager(
    private val firebaseAuth: FirebaseAuth, // Instancia de FirebaseAuth para autenticación
) {
    // LiveData para exponer el token de manera observable
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    // Función para obtener el token de ID de Firebase de forma asíncrona
    fun getIdToken() {
        // Obtener el token del usuario actual de Firebase
        firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si la tarea es exitosa, almacenar el token en _token y hacer un log
                _token.value = task.result?.token
                Log.d("token", _token.value.toString())
            } else {
                // Si ocurre un error, hacer log del error y asignar null a _token
                Log.e("token", "Error al obtener el token", task.exception)
                _token.value = null
            }
        }
    }

    // Función suspendida para obtener el token de forma síncrona en una corrutina
    suspend fun getTokenSynchronously(): String? =
        suspendCancellableCoroutine { continuation ->
            // Obtener el token del usuario actual de Firebase
            firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si la tarea es exitosa, almacenar el token en _token y continuar la corrutina
                    val token = task.result?.token
                    _token.value = token
                    continuation.resume(token)
                } else {
                    // Si ocurre un error, hacer log del error y continuar la corrutina con null
                    Log.e("token", "Error al obtener el token", task.exception)
                    continuation.resume(null)
                }
            }
        }
}
