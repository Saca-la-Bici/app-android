package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQ
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class FAQAPIClient(
    private val firebaseTokenManager: FirebaseTokenManager,
) {
    private lateinit var api: FAQAPIService

    suspend fun getFAQList(): List<FAQBase> {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica

        return if (token != null) {
            api = FAQModuleDI(token)
            try {
                api.getFAQList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

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
}
