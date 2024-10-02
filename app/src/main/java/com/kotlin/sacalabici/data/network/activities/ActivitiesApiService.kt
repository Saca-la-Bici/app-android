package com.kotlin.sacalabici.data.network.activities

import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.JoinActivityRequest
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ActivitiesApiService {
    @GET("actividades/consultar/eventos")
    suspend fun getEventos(): EventosBase

    @GET("actividades/consultar/rodadas")
    suspend fun getRodadas(): RodadasBase

    @GET("actividades/consultar/talleres")
    suspend fun getTalleres(): TalleresBase

    @POST("actividades/inscripcion/inscribir")
    suspend fun PostJoinActivity(@Body request: JoinActivityRequest)
}