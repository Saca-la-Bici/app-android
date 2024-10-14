package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FAQAPIService {
    // Consulta la lista de preguntas frecuentes
    @GET("preguntasFrecuentes/consultar")
    suspend fun getFAQList(): FAQObjectBase

    @POST("preguntasFrecuentes/registrar")
    suspend fun postFAQ(
        @Body faq: FAQBase,
    ): FAQBase
}
