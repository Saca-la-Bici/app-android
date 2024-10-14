package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.DeleteResponse
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
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

    suspend fun deleteFAQ(IdPregunta: Int): DeleteResponse {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = FAQModuleDI(token)
        return try {
            val response = api.deleteFAQ(IdPregunta)
            response
        } catch (e: Exception) {
            e.printStackTrace()
            DeleteResponse(false, 0)
        }
    }

    suspend fun postFAQ(faq: FAQBase): FAQBase? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = FAQModuleDI(token)

        return try {
            api.postFAQ(faq)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
