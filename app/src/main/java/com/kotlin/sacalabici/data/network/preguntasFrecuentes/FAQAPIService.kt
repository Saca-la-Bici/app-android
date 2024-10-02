package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import retrofit2.http.GET

interface FAQAPIService {
    // Consulta la lista de preguntas frecuentes
    @GET("preguntasFrecuentes/consultar")
    suspend fun getFAQList(): FAQBase

    /*
    @POST("preguntasFrecuentes/registrar")
    suspend fun postFAQ(
        @Body faq: FAQ,
    ): FAQ
     */
}
