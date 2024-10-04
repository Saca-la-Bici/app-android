package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreguntasFrecuentesAPIService {

    @GET("consultar")
    suspend fun consultarPreguntasFrecuentes(): List<FAQBase>

    @POST("registrar")
    suspend fun registrarPreguntaFrecuente(
        @Body preguntaFrecuente: FAQBase
    ): FAQBase

    @GET("preguntaFrecuente/consultarIndividual/{IdPregunta}")
    suspend fun consultarPreguntaFrecuenteInd(
        @Path("IdPregunta") IdPregunta :Int): FAQBase

    @PUT("consultarIndividual/{IdPregunta}/modificacion")
    suspend fun modificarPreguntaFrecuente(
        @Path("IdPregunta") IdPregunta: Int,
        Pregunta: String,
        Respuesta: String,
        Tema: String
    ):FAQBase
}