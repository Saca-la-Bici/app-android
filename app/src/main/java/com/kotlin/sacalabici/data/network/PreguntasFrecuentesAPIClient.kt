package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.models.PreguntaFrecuente

class PreguntasFrecuentesAPIClient {

    private val api: PreguntasFrecuentesAPIService = PreguntasFrecuentesModuleDI()
    suspend fun consultarPreguntasFrecuentesList(): List<PreguntaFrecuente>? {
        return try {
            api.consultarPreguntasFrecuentes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: PreguntaFrecuente):PreguntaFrecuente? {
        return try {
            api.registrarPreguntaFrecuente(preguntaFrecuente)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
