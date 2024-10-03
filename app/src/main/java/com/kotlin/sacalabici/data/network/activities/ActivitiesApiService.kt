package com.kotlin.sacalabici.data.network.activities

import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import retrofit2.http.Body
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import com.kotlin.sacalabici.data.network.model.Informacion
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.Date

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
    
    @GET("getPermissions")
    suspend fun getPermissions(): PermissionsObject
}