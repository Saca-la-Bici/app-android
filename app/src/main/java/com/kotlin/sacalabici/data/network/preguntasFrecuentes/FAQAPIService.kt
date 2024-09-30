package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FAQAPIService {
    // Consulta la lista de preguntas frecuentes
    @GET("preguntasFrecuentes/consultar")
    suspend fun consultFAQ(
        @Query("limit") limit: Int,
    ): FAQObject

    @POST("preguntasFrecuentes/registrar")
    suspend fun registrarPreguntaFrecuente(
        @Body preguntaFrecuente: PreguntaFrecuente,
    ): PreguntaFrecuente
}
