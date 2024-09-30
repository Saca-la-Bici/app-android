package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.models.profile.ProfileBase
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("perfil/consultar/")
    suspend fun getUsuario(): ProfileBase
}

