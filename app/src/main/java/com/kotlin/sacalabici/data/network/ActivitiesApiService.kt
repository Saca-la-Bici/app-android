package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.ActivityBase
import retrofit2.http.GET

interface ActivitiesApiService {
    @GET("actividades/consultar/eventos")
    suspend fun getEventos(): List<ActivityBase>

    @GET("actividades/consultar/rodadas")
    suspend fun getRodadas(): List<ActivityBase>

    @GET("actividades/consultar/talleres")
    suspend fun getTalleres(): List<ActivityBase>
}