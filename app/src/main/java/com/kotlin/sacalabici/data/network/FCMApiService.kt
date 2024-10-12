package com.kotlin.sacalabici.data.network

import retrofit2.http.Body
import retrofit2.http.POST

data class FCMTokenRequest(val fcmToken: String)

interface FCMApiService {
    @POST("session/actualizarTokenNotificacion")
    suspend fun postFCMToken(
        @Body fcmToken: FCMTokenRequest
    ): Unit
}