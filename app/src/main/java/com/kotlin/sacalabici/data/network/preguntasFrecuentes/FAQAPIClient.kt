package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente

class FAQAPIClient {

    private val api: FAQAPIService = FAQModuleDI()

    // Obtener la lista de preguntas frecuentes
    suspend fun consultFAQList(): FAQObject? {
        return try {
            api.consultFAQ()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Registrar una nueva pregunta frecuente
    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? {
        return try {
            api.registrarPreguntaFrecuente(preguntaFrecuente)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

