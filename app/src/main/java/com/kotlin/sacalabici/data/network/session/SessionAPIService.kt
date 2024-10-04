package com.kotlin.sacalabici.data.network.session

import com.kotlin.sacalabici.data.models.session.PerfilCompletoObject
import com.kotlin.sacalabici.data.models.user.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SessionAPIService {
    //http://localhost:7070/session/registrarUsuario
    @POST("session/registrarUsuario")
    suspend fun registerUser(@Body user: User): User

    //http://localhost:7070/perfil/perfilCompleto
    @GET("session/perfilCompleto")
    suspend fun getUser(): PerfilCompletoObject
}
