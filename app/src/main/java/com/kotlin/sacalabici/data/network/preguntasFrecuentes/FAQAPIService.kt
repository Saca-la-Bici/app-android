package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.DeleteResponse
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import retrofit2.HttpException
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

interface FAQAPIService {
    // Consulta la lista de preguntas frecuentes
    @GET("preguntasFrecuentes/consultar")
    suspend fun getFAQList(): FAQObjectBase

    @DELETE("preguntasFrecuentes/eliminar/{IdPregunta}")
    suspend fun deleteFAQ(@Path("IdPregunta") IdPregunta: Int): DeleteResponse

}
