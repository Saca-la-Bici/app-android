package com.kotlin.sacalabici.data.network

import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApiService {
    @POST("session/actualizarTokenNotificacion")
    suspend fun postFCMToken(
        @Body token: String
    ): Unit
}