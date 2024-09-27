package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivitiesNetworkModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    private fun createOkHttpClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()
    }

    operator fun invoke(token: String?): ActivitiesApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.ANNOUNCEMENT_BASE_URL)
            .client(createOkHttpClient(token))
            .addConverterFactory(gsonFactory)
            .build()
            .create(ActivitiesApiService::class.java)
    }
}