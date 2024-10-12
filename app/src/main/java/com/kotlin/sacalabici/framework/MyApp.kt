package com.kotlin.sacalabici.framework

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kotlin.sacalabici.data.network.FCMApiService
import com.kotlin.sacalabici.data.network.FCMNetworkModuleDI
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
        FirebaseApp.initializeApp(this)
        Log.d("FCM", "FirebaseApp initialized in MyApplication")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseTokenManager = FirebaseTokenManager(firebaseAuth)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Firebase Messaging token: $token")
                // Launch a coroutine to send the token to the server
                CoroutineScope(Dispatchers.IO).launch {
                    sendRegistrationToServer(token)
                }
            } else {
                Log.e("FCM", "Error al obtener el token de Firebase Messaging", task.exception)
            }
        }
    }

    private suspend fun sendRegistrationToServer(FCMToken: String?): Unit? {
        val authToken = firebaseTokenManager.getTokenSynchronously()
        Log.d("FCM", "Token de autenticación: $authToken")
        api = FCMNetworkModuleDI(authToken)

        return try {
            api.postFCMToken(FCMToken!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}