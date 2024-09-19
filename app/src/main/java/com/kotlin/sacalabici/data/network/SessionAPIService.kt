package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SessionAPIService {
    //http://localhost:7070/session/registrarUsuario
    @POST("session/registrarUsuario")
    suspend fun registerUser(@Body user: User): User
}
