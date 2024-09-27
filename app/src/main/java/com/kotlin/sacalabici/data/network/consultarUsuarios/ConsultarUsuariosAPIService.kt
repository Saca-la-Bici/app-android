package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultarUsuariosAPIService {
    //http://localhost:8080/perfil/consultarUsuarios?page=1&limit=10&roles=Rol,Rol&firebaseUID=firebaseUID
    @GET("perfil/consultarUsuarios")
    suspend fun getUsuarios(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("roles") roles: String,
        @Query("firebaseUID") firebaseUID: String,
    ): ConsultarUsuariosObject

    @GET("perfil/buscarUsuarios")
    suspend fun searchUser(
        @Query("query") username: String
    ): ConsultarUsuariosObject

}