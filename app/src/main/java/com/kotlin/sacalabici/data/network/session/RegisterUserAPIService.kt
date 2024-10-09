package com.kotlin.sacalabici.data.network.session

import com.kotlin.sacalabici.data.models.session.GetUsernameObject
import retrofit2.http.GET
import retrofit2.http.Query

interface RegisterUserAPIService {
    //http://localhost:7070/session/getUsername
    @GET("session/getUsername")
    suspend fun verifyUser(
        @Query("username") username: String,
    ): GetUsernameObject
}