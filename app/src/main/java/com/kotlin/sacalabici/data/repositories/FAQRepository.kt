package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.FAQAPIClient

// Intermediario entre el Requirement y API
class FAQRepository {
    private val apiFAQ = FAQAPIClient()

    // Delegar la solicitud a la API y devolver un FAQObject, o null.
    suspend fun consultFAQList(limit: Int): com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject? = apiFAQ.consultFAQList(limit)

    // Registrar una nueva pregunta frecuente
    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? =
        apiFAQ.registrarPreguntaFrecuente(preguntaFrecuente)
}
