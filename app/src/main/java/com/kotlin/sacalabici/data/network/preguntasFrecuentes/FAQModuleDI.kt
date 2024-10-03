package com.kotlin.sacalabici.data.network.preguntasFrecuentes

import com.kotlin.sacalabici.data.network.AuthInterceptor
import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FAQModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private fun createOkHttpClient(token: String?): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

    operator fun invoke(token: String?): FAQAPIService =
        Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .client(createOkHttpClient(token))
            .addConverterFactory(gsonFactory)
            .build()
            .create(FAQAPIService::class.java)
}
