package com.kotlin.sacalabici.data.repositories

import android.util.Log
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.PreguntasFrecuentesAPIClient

class PreguntasFrecuentesRepository {
    private val apiPreguntasFrecuentes = PreguntasFrecuentesAPIClient()

    suspend fun consultarPreguntasFrecuentesList(): List<FAQBase>? {
        return apiPreguntasFrecuentes.consultarPreguntasFrecuentesList()
    }

    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: FAQBase): FAQBase? {
        return apiPreguntasFrecuentes.registrarPreguntaFrecuente(preguntaFrecuente)
    }

    suspend fun consultarPreguntaFrecuenteInd(IdPregunta:Int): FAQBase{
        Log.d("ayuda","Entra repo")
        return apiPreguntasFrecuentes.consultarPreguntaFrecuenteInd(IdPregunta)
    }

    suspend fun modificarPreguntaFrecuente(IdPregunta: Int, Pregunta: String, Respuesta:String, Tema:String): FAQBase? {
        return apiPreguntasFrecuentes.modificarPreguntaFrecuente(IdPregunta,Pregunta,Respuesta,Tema)
    }

}