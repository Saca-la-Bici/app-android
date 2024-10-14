package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.CancelActivityRequest
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.JoinActivityRequest
import com.kotlin.sacalabici.data.models.activities.OneActivityBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import retrofit2.http.Body
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import retrofit2.http.Query
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.AttendanceRequest
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.routes.RouteBase
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @Multipart
    @POST("actividades/registrar/taller")
    suspend fun postActivityTaller(
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part imagen: MultipartBody.Part?
    ): ActivityModel

    @Multipart
    @POST("actividades/registrar/evento")
    suspend fun postActivityEvento(
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part imagen: MultipartBody.Part?
    ): ActivityModel

    @Multipart
    @POST("actividades/registrar/rodada")
    suspend fun postActivityRodada(
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part("ruta") ruta: String,
        @Part imagen: MultipartBody.Part?
    ): Rodada

    @POST("actividades/inscripcion/inscribir")
    suspend fun PostJoinActivity(@Body request: JoinActivityRequest)

    @POST("actividades/cancelarAsistencia/cancelar")
    suspend fun PostCancelActivity(@Body request: CancelActivityRequest)

    @PATCH("rodadas/verificarAsistencia")
    suspend fun PostValidateAttendance(@Body request: AttendanceRequest)

    @PUT("rodadas/iniciarRodada/{id}")
    suspend fun postLocation(
        @Path("id") id: String,
        @Body location: LocationR  // Usa la clase Location definida en tu modelo
    ): Response<Void>

    @GET("rodadas/obtenerRodadaPorId/{id}")
    suspend fun getRodadaInfo(@Path("id") id: String): RodadaInfoBase

    @GET("rodadas/obtenerUbicacion/{id}")
    suspend fun getUbicacion(@Path("id") id: String): List<LocationR>

    @DELETE("rodadas/eliminarUbicacion/{id}")
    suspend fun eliminarUbicacion(@Path("id") id: String): RouteBase
}