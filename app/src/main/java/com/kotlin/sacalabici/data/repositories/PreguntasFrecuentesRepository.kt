package com.kotlin.sacalabici.data.repositories

import android.util.Log
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

    suspend fun consultarPreguntaFrecuenteInd(IdPregunta:Int): PreguntaFrecuente{
        Log.d("ayuda","Entra repo")
        return apiPreguntasFrecuentes.consultarPreguntaFrecuenteInd(IdPregunta)
    }

    suspend fun modificarPreguntaFrecuente(IdPregunta: Int, Pregunta: String, Respuesta:String, Tema:String): PreguntaFrecuente? {
        return apiPreguntasFrecuentes.modificarPreguntaFrecuente(IdPregunta,Pregunta,Respuesta,Tema)
    }

}