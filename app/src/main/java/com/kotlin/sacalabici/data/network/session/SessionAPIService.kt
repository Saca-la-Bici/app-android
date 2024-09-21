package com.kotlin.sacalabici.data.network.session

import com.kotlin.sacalabici.data.models.session.User
import retrofit2.http.Body
import retrofit2.http.POST

interface SessionAPIService {
    //http://localhost:7070/session/registrarUsuario
    @POST("session/registrarUsuario")
    suspend fun registerUser(@Body user: User): User
}
