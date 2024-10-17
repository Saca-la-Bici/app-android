package com.kotlin.sacalabici.data.network.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import com.kotlin.sacalabici.data.models.activities.ActivitiesBase
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.data.network.MultipartManager
import com.kotlin.sacalabici.data.network.announcements.AnnouncementNetworkModuleDI


class ProfileApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    // Inicializar el cliente Retrofit con el token
    private lateinit var api: ProfileApiService
    private val multipartManager = MultipartManager()

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

    suspend fun patchProfile(profile: Profile, context: Context): Profile?{ // Convierte el objeto profile en variables individuales para mandarlo como parámetros en el body del api request
        val token = firebaseTokenManager.getTokenSynchronously()
        api = ProfileNetworkModuleDI(token)
        val username = profile.username
        val nombre = profile.nombre
        val tipoSangre = profile.tipoSangre
        val numeroEmergencia = profile.numeroEmergencia

        val file = profile.imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return try {
            api.patchProfile(username, nombre, tipoSangre, numeroEmergencia, img)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getActividades(): ActivitiesBase? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ProfileNetworkModuleDI(token)
            try {
                val activitiesBase = api.getActividades()
                activitiesBase
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun deleteProfile(): Boolean {
        val token: String?
        try{
            token = firebaseTokenManager.getTokenSynchronously()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        api = ProfileNetworkModuleDI(token)
        return try {
            api.deleteProfile().isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}
