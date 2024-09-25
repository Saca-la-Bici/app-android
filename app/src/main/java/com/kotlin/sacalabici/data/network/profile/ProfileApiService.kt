package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.network.model.profile.Profile
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("perfil/consultar/{id}")
    suspend fun getUsuario(
        @Path("id") id: String
    ): Profile
}

