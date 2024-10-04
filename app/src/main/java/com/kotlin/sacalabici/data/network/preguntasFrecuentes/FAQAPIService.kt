package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import retrofit2.http.GET

interface FAQAPIService {
    // Consulta la lista de preguntas frecuentes
    @GET("preguntasFrecuentes/consultar")
    suspend fun getFAQList(): FAQObjectBase

    /*
    @POST("preguntasFrecuentes/registrar")
    suspend fun postFAQ(
        @Body faq: FAQ,
    ): FAQ
     */
}
