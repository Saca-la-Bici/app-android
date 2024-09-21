package com.kotlin.sacalabici.data.network.session

import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SessionModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private fun getOkHttpClient(idToken: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                idToken?.let {
                    requestBuilder.addHeader("Content-Type", "application/json")
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    fun createSessionAPIService(idToken: String?): SessionAPIService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getOkHttpClient(idToken))
            .addConverterFactory(gsonFactory)
            .build()
            .create(SessionAPIService::class.java)
    }
}

