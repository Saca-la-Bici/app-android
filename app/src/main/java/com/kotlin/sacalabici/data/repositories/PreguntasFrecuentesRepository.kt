package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.PreguntasFrecuentesAPIClient

class PreguntasFrecuentesRepository {
    private val apiPreguntasFrecuentes = PreguntasFrecuentesAPIClient()

    suspend fun consultarPreguntasFrecuentesList(): List<PreguntaFrecuente>? {
        return apiPreguntasFrecuentes.consultarPreguntasFrecuentesList()
    }

    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? {
        return apiPreguntasFrecuentes.registrarPreguntaFrecuente(preguntaFrecuente)
    }
}