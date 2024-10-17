package com.kotlin.sacalabici.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Crear una nueva solicitud basada en la original, agregando el encabezado de autorización
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token") // Agregar el token como encabezado
            .build()
        // Proseguir con la solicitud modificada y devolver la respuesta
        return chain.proceed(request)
    }
}