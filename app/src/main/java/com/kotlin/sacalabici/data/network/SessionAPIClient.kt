package com.kotlin.sacalabici.data.network

import android.util.Log
import com.kotlin.sacalabici.data.models.User

class SessionAPIClient {
    private lateinit var api: SessionAPIService

    suspend fun registerUser(user: User): User? {
        api = SessionModuleDI()
        return try {
            api.registerUser(user)
        } catch (e: Exception) {
            Log.e("SessionAPIClient", "Error al registrar usuario", e)
            e.printStackTrace()
            null
        }
    }
}