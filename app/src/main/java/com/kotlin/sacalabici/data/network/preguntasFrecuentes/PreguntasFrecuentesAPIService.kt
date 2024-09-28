package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PreguntasFrecuentesAPIService {

    @GET("consultar")
    suspend fun consultarPreguntasFrecuentes(): List<PreguntaFrecuente>

    @POST("registrar")
    suspend fun registrarPreguntaFrecuente(
        @Body preguntaFrecuente: PreguntaFrecuente
    ): PreguntaFrecuente
}