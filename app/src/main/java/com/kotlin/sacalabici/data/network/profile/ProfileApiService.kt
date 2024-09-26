package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.network.model.ProfileBase
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("perfil/consultar/{id}")
    suspend fun getUsuario(
        @Path("id") id: String
    ): ProfileBase
}

