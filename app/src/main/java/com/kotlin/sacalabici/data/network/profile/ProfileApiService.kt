package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.models.profile.Profile

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileApiService {
    @GET("perfil/consultar/")
    suspend fun getUsuario(): ProfileBase

    @PATCH("perfil/modificar/")
    suspend fun  patchProfile(
        @Body profile: Profile
    ): Profile
}



