package com.kotlin.sacalabici.data.network.activities

import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ActivitiesApiService {
    @GET("actividades/consultar/eventos")
    suspend fun getEventos(): EventosBase

    @GET("actividades/consultar/rodadas")
    suspend fun getRodadas(): RodadasBase

    @GET("actividades/consultar/talleres")
    suspend fun getTalleres(): TalleresBase

    @GET("actividades/consultar")
    suspend fun getActivityById(@Query("id") id: String): Activity

    @GET("getPermissions")
    suspend fun getPermissions(): PermissionsObject
}