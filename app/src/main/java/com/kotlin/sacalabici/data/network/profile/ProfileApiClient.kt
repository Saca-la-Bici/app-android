package com.kotlin.sacalabici.data.network.profile

import android.util.Log
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.data.network.announcements.AnnouncementNetworkModuleDI
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement

class ProfileApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    // Inicializar el cliente Retrofit con el token
    private lateinit var api: ProfileApiService

    suspend fun getUsuario(): ProfileBase? {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
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

    suspend fun patchProfile(profile: Profile): Profile?{
        val token = firebaseTokenManager.getTokenSynchronously()
        api = ProfileNetworkModuleDI(token)
        return try {
            api.patchProfile(profile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
