package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import android.util.Log
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente

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

    suspend fun consultarPreguntaFrecuenteInd(IdPregunta:Int): PreguntaFrecuente{
        Log.d("ayuda","Entra ApiClient")
        return try{
            Log.d("ayuda","Regresó al ApiCLient")
            api.consultarPreguntaFrecuenteInd(IdPregunta)
        } catch(err: Exception){
            throw err
        }
    }

    suspend fun modificarPreguntaFrecuente(IdPregunta: Int, Pregunta:String, Respuesta:String, Tema:String):PreguntaFrecuente?{
        return try{
            api.modificarPreguntaFrecuente(IdPregunta,Pregunta,Respuesta,Tema)
        } catch(e : Exception){
            e.printStackTrace()
            null
        }
    }
}