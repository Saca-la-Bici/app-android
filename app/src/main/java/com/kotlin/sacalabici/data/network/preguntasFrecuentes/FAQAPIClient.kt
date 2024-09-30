package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente

class FAQAPIClient {
    private val api: FAQAPIService = FAQModuleDI()

    // Obtener la lista de preguntas frecuentes
    suspend fun consultFAQList(limit: Int): FAQObject? =
        try {
            api.consultFAQ(limit)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    // Registrar una nueva pregunta frecuente
    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? =
        try {
            api.registrarPreguntaFrecuente(preguntaFrecuente)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}
