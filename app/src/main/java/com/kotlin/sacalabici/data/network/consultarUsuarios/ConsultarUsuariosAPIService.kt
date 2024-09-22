package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultarUsuariosAPIService {
    //http://localhost:8080/perfil/consultarUsuarios
    @GET("perfil/consultarUsuarios")
    suspend fun getUsuarios(
        @Query("limit") limit: Int
    ): ConsultarUsuariosObject

    @GET("perfil/buscarUsuarios")
    suspend fun searchUser(
        @Query("query") username: String
    ): ConsultarUsuariosObject

}