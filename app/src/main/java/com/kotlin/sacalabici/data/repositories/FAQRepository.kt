package com.kotlin.sacalabici.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQ
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.FAQAPIClient

// Intermediario entre el Requirement y API
class FAQRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiFAQ = FAQAPIClient(firebaseTokenManager)

    suspend fun getFAQList(): List<FAQBase> = apiFAQ.getFAQList()

    suspend fun postFAQ(announcement: FAQ): FAQ? = apiFAQ.postFAQ(announcement)
}
