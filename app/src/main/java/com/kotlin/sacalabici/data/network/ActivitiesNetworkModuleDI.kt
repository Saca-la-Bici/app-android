package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ActivitiesNetworkModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    operator fun invoke(): ActivitiesApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.ANNOUNCEMENT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonFactory)
            .build()
            .create(ActivitiesApiService::class.java)
    }
}