package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultarUsuariosAPIService {
    //http://localhost:8080/perfil/consultarUsuarios?page=1&limit=10&roles=Rol,Rol
    @GET("perfil/consultarUsuarios")
    suspend fun getUsuarios(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("roles") roles: String,
    ): ConsultarUsuariosObject

    //http://localhost:8080/perfil/buscarUsuarios?query=name&firebaseUID=firebaseUID
    @GET("perfil/buscarUsuarios")
    suspend fun searchUser(
        @Query("query") username: String,
    ): ConsultarUsuariosObject
}