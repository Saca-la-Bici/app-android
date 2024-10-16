package com.kotlin.sacalabici.framework

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kotlin.sacalabici.data.network.FCMApiService
import com.kotlin.sacalabici.data.network.FCMNetworkModuleDI
import com.kotlin.sacalabici.data.network.FCMTokenRequest
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {
    private lateinit var api: FCMApiService
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseTokenManager: FirebaseTokenManager

    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseTokenManager = FirebaseTokenManager(firebaseAuth)

        // Obtiene el token de FCM de Firebase Messaging
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                CoroutineScope(Dispatchers.IO).launch {
                    sendRegistrationToServer(token)
                }
            } else {
                Log.e("FCM", "Error al obtener el token de Firebase Messaging", task.exception)
            }
        }
    }

    // Método para enviar el token de FCM al servidor
    private suspend fun sendRegistrationToServer(FCMToken: String?): Unit? {
        // Obtiene el token de autenticación
        val authToken = firebaseTokenManager.getTokenSynchronously()
        api = FCMNetworkModuleDI(authToken)

        return try {
            api.postFCMToken(FCMTokenRequest(FCMToken!!))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}