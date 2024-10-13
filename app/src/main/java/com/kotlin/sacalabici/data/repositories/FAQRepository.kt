package com.kotlin.sacalabici.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.DeleteResponse
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.FAQAPIClient

// Intermediario entre el Requirement y API
class FAQRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiFAQ = FAQAPIClient(firebaseTokenManager)

    suspend fun getFAQList(): FAQObjectBase? =
        try {
            // Realiza la consulta
            val response = apiFAQ.getFAQList()
            Log.d("FAQRepository", "Consulta exitosa: $response")
            response
        } catch (e: Exception) {
            Log.e("Falla en getFAQList", "Error en la consulta de getFAQList: ${e.message}")
            null
        }

    suspend fun deleteFAQ(IdPregunta: Int): DeleteResponse {
        return try {
            Log.d("FAQRepository", "Calling API to delete FAQ with Id: $IdPregunta")
            val response = apiFAQ.deleteFAQ(IdPregunta)
            Log.d("FAQRepository", "Delete response: $response")
            response
        } catch (e: Exception) {
            Log.e("FAQRepository", "Error in deleteFAQ: ${e.message}")
            DeleteResponse(false, 0)
        }
    }
    // suspend fun postFAQ(announcement: FAQ): FAQ? = apiFAQ.postFAQ(announcement)
}
