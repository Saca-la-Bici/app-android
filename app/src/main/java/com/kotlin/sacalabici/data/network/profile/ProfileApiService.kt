package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.models.activities.ActivitiesBase
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.data.models.activities.ActivityBase

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Part

interface ProfileApiService {
    @GET("perfil/consultar/")
    suspend fun getUsuario(): ProfileBase

    @Multipart
    @PATCH("perfil/modificar/")
    suspend fun  patchProfile(
        @Part("username") username: String,
        @Part("nombre") nombre: String,
        @Part("tipoSangre") tipoSangre: String,
        @Part("numeroEmergencia") numeroEmergencia: String,
        @Part imagen: MultipartBody.Part?
    ): Profile

    @GET("perfil/consultarActividadesInscritas/")
    suspend fun getActividades(): ActivitiesBase
}



