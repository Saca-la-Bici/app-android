package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class FAQAPIClient(
    private val firebaseTokenManager: FirebaseTokenManager,
) {
    private lateinit var api: FAQAPIService

    suspend fun getFAQList(): FAQObjectBase? {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        api = FAQModuleDI(token)

        return try {
            api.getFAQList()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    /*
    suspend fun postFAQ(announcement: FAQ): FAQ? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = FAQModuleDI(token)
        return try {
            api.postFAQ(announcement)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
     */
}
