package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import android.util.Log
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase

class PreguntasFrecuentesAPIClient {

    private lateinit var api: PreguntasFrecuentesAPIService

    suspend fun consultarPreguntasFrecuentesList(): List<FAQBase>? {
        return try {
            api.consultarPreguntasFrecuentes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registrarPreguntaFrecuente(preguntaFrecuente: FAQBase):FAQBase? {
        return try {
            api.registrarPreguntaFrecuente(preguntaFrecuente)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun consultarPreguntaFrecuenteInd(IdPregunta:Int): FAQBase{
        return try{
            api.consultarPreguntaFrecuenteInd(IdPregunta)
        } catch(err: Exception){
            throw err
        }
    }

    suspend fun modificarPreguntaFrecuente(IdPregunta: Int, Pregunta:String, Respuesta:String, Tema:String):FAQBase?{
        return try{
            api.modificarPreguntaFrecuente(IdPregunta,Pregunta,Respuesta,Tema)
        } catch(e : Exception){
            e.printStackTrace()
            null
        }
    }
}