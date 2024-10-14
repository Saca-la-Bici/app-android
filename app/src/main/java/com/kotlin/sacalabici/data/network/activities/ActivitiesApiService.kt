package com.kotlin.sacalabici.data.network.activities

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
import com.kotlin.sacalabici.data.network.model.ActivityData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import retrofit2.http.Query
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.AttendanceRequest
import com.kotlin.sacalabici.data.models.activities.LocationR
import retrofit2.Response
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

    @Multipart
    @PATCH("actividades/modificar/taller")
    suspend fun patchActivityTaller(
        @Query("id") id: RequestBody,
        @Part("informacion[titulo]") titulo: RequestBody,
        @Part("informacion[fecha]") fecha: RequestBody,
        @Part("informacion[hora]") hora: RequestBody,
        @Part("informacion[duracion]") duracion: RequestBody,
        @Part("informacion[ubicacion]") ubicacion: RequestBody,
        @Part("informacion[descripcion]") descripcion: RequestBody,
        @Part("informacion[tipo]") tipo: RequestBody,
        @Part imagen: MultipartBody.Part?,
        @Part("informacion[personasInscritas]") peopleEnrolled: RequestBody,
        @Part("informacion[estado]") state: RequestBody,
        @Part("informacion[foro]") foro: RequestBody?,
        @Part usuariosInscritos: List<MultipartBody.Part>?
    ): ActivityData

    @Multipart
    @PATCH("actividades/modificar/evento")
    suspend fun patchActivityEvento(
        @Query("id") id: RequestBody,
        @Part("informacion[titulo]") titulo: RequestBody,
        @Part("informacion[fecha]") fecha: RequestBody,
        @Part("informacion[hora]") hora: RequestBody,
        @Part("informacion[duracion]") duracion: RequestBody,
        @Part("informacion[ubicacion]") ubicacion: RequestBody,
        @Part("informacion[descripcion]") descripcion: RequestBody,
        @Part("informacion[tipo]") tipo: RequestBody,
        @Part imagen: MultipartBody.Part?,
        @Part("informacion[personasInscritas]") peopleEnrolled: RequestBody,
        @Part("informacion[estado]") state: RequestBody,
        @Part("informacion[foro]") foro: RequestBody?,
        @Part usuariosInscritos: List<MultipartBody.Part>?
    ): ActivityData

    @Multipart
    @PATCH("actividades/modificar/rodada")
    suspend fun patchActivityRodada(
        @Query("id") id: RequestBody,
        @Part("informacion[titulo]") titulo: RequestBody,
        @Part("informacion[fecha]") fecha: RequestBody,
        @Part("informacion[hora]") hora: RequestBody,
        @Part("informacion[duracion]") duracion: RequestBody,
        @Part("informacion[ubicacion]") ubicacion: RequestBody,
        @Part("informacion[descripcion]") descripcion: RequestBody,
        @Part("informacion[tipo]") tipo: RequestBody,
        @Part imagen: MultipartBody.Part?,
        @Part("informacion[personasInscritas]") peopleEnrolled: RequestBody,
        @Part("informacion[estado]") state: RequestBody,
        @Part("informacion[foro]") foro: RequestBody?,
        @Part usuariosInscritos: List<MultipartBody.Part>?,
        @Part("ruta") ruta: RequestBody?
    ): ActivityData


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
}