package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.OneActivityBase
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivitiesApiService {
    @GET("actividades/consultar/eventos")
    suspend fun getEventos(): EventosBase

    @GET("actividades/consultar/rodadas")
    suspend fun getRodadas(): RodadasBase

    @GET("actividades/consultar/talleres")
    suspend fun getTalleres(): TalleresBase

    @GET("actividades/consultar")
    suspend fun getActivityById(@Query("id") id: String): OneActivityBase

    @GET("getPermissions")
    suspend fun getPermissions(): PermissionsObject

    @PUT("rodadas/iniciarRodada/{id}")
    suspend fun postLocation(
        @Path("id") id: String,
        @Body location: LocationR  // Usa la clase Location definida en tu modelo
    ): Response<Void>

    @GET("rodadas/obtenerRodadaPorId/{id}")
    suspend fun getRodadaInfo(@Path("id") id: String): RodadaInfoBase

    @GET("rodadas/obtenerUbicacion/{id}")
    suspend fun getUbicacion(@Path("id") id: String): List<LocationR>
}