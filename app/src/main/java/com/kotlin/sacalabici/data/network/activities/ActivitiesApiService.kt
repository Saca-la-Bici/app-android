package com.kotlin.sacalabici.data.network.activities

import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
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

    @POST("actividades/registrar/taller")
    suspend fun postActivityTaller(@Body taller: ActivityModel): ActivityModel

    @POST("actividades/registrar/evento")
    suspend fun postActivityEvento(@Body evento: ActivityModel): ActivityModel

    @POST("actividades/registrar/rodada")
    suspend fun postActivityRodada(@Body rodada: Rodada): Rodada
}