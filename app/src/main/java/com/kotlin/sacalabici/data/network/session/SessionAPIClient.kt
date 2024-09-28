package com.kotlin.sacalabici.data.network.session

import android.util.Log
import com.kotlin.sacalabici.data.models.session.PerfilCompletoObject
import com.kotlin.sacalabici.data.models.user.User

class SessionAPIClient(private val idToken: String?) {

    private val api: SessionAPIService by lazy {
        SessionModuleDI.createSessionAPIService(idToken)
    }

    suspend fun registerUser(user: User): User? {
        return try {
            api.registerUser(user)
        } catch (e: Exception) {
            Log.e("SessionAPIClient", "Error al registrar usuario", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun getUser(): PerfilCompletoObject? {
        return try {
            api.getUser()
        } catch (e: Exception) {
            Log.e("SessionAPIClient", "Error al obtener usuario", e)
            e.printStackTrace()
            null
        }
    }
}
