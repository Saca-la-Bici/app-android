package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.network.AuthInterceptor
import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MedalsNetworkModuleDI {

    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private fun createOkHttpClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()
    }

    operator fun invoke(token: String?): MedalsApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(createOkHttpClient(token))
            .addConverterFactory(gsonFactory)
            .build()
            .create(MedalsApiService::class.java)
    }
}