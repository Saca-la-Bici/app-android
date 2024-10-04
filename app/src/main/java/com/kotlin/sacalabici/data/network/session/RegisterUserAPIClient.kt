package com.kotlin.sacalabici.data.network.session

import android.util.Log
import com.kotlin.sacalabici.data.models.session.GetUsernameObject

class RegisterUserAPIClient {
    private val api: RegisterUserAPIService by lazy { RegisterUserModuleDI() }

    suspend fun verifyUser(username: String): GetUsernameObject? {
        return try {
            val response = api.verifyUser(username)
            response
        } catch (e: Exception) {
            Log.e("SessionAPIClient", "Error al obtener el usuario", e)
            e.printStackTrace()
            null
        }
    }
}