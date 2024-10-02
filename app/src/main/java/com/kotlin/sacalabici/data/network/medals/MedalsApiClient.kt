package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.models.medals.MedalObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class MedalsApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    private lateinit var api: MedalsApiService

    suspend fun getMedalsList(): MedalObjectBase? {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        api = MedalsNetworkModuleDI(token)
        return try {
            api.getMedalsList()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }
}
