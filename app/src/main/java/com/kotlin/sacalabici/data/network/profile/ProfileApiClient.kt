package com.kotlin.sacalabici.data.network.profile

import android.util.Log
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class ProfileApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    // Inicializar el cliente Retrofit con el token
    private lateinit var api: ProfileApiService

    suspend fun getUsuario(): ProfileBase? {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
            Log.d("ProfileApiClient", "Token obtenido correctamente: $token")
            api = ProfileNetworkModuleDI(token)
            try {
                // Llamada al API para obtener el usuario
                api.getUsuario()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            // Si no se pudo obtener el token, retornar null o manejar el error de alguna otra forma
            println("Error: No se pudo obtener el token de Firebase.")
            null
        }
    }
}
