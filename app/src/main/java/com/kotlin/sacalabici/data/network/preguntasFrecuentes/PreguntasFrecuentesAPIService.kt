package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreguntasFrecuentesAPIService {

    @GET("consultar")
    suspend fun consultarPreguntasFrecuentes(): List<PreguntaFrecuente>

    @POST("registrar")
    suspend fun registrarPreguntaFrecuente(
        @Body preguntaFrecuente: PreguntaFrecuente
    ): PreguntaFrecuente

    @GET("preguntaFrecuente/consultarIndividual/{IdPregunta}")
    suspend fun consultarPreguntaFrecuenteInd(
        @Path("IdPregunta") IdPregunta :Int): PreguntaFrecuente

    @PUT("consultarIndividual/{IdPregunta}/modificacion")
    suspend fun modificarPreguntaFrecuente(
        @Path("IdPregunta") IdPregunta: Int,
        Pregunta: String,
        Respuesta: String,
        Tema: String
    ):PreguntaFrecuente
}