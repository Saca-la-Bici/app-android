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
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ActivitiesApiService {
    @GET("actividades/consultar/eventos")
    suspend fun getEventos(): EventosBase

    @GET("actividades/consultar/rodadas")
    suspend fun getRodadas(): RodadasBase

    @GET("actividades/consultar/talleres")
    suspend fun getTalleres(): TalleresBase

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

    @GET("actividades/consultar")
    suspend fun getActivityById(@Query("id") id: String): OneActivityBase

    @GET("getPermissions")
    suspend fun getPermissions(): PermissionsObject

    @POST("actividades/inscripcion/inscribir")
    suspend fun PostJoinActivity(@Body request: JoinActivityRequest)

    @POST("actividades/cancelarAsistencia/cancelar")
    suspend fun PostCancelActivity(@Body request: CancelActivityRequest)

    @Multipart
    @PATCH("actividades/modificar/taller")
    suspend fun patchActivityTaller(
        @Query("id") id: String,
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part imagen: MultipartBody.Part?,
        @Part("peopleEnrolled") peopleEnrolled: Int,
        @Part("state") state: Boolean,
        @Part("foro") foro: String?,
        @Part("register") register: List<String>?
    ): ActivityData

    @Multipart
    @PATCH("actividades/modificar/evento")
    suspend fun patchActivityEvento(
        @Query("id") id: String,
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part imagen: MultipartBody.Part?,
        @Part("peopleEnrolled") peopleEnrolled: Int,
        @Part("state") state: Boolean,
        @Part("foro") foro: String?,
        @Part("register") register: List<String>?
    ): ActivityData

    @Multipart
    @PATCH("actividades/modificar/rodada")
    suspend fun patchActivityRodada(
        @Query("id") id: String,
        @Part("informacion[titulo]") titulo: String,
        @Part("informacion[fecha]") fecha: String,
        @Part("informacion[hora]") hora: String,
        @Part("informacion[duracion]") duracion: String,
        @Part("informacion[ubicacion]") ubicacion: String,
        @Part("informacion[descripcion]") descripcion: String,
        @Part("informacion[tipo]") tipo: String,
        @Part imagen: MultipartBody.Part?,
        @Part("peopleEnrolled") peopleEnrolled: Int,
        @Part("state") state: Boolean,
        @Part("foro") foro: String?,
        @Part("register") register: List<String>?,
        @Part("ruta") ruta: String?
    ): ActivityData

}