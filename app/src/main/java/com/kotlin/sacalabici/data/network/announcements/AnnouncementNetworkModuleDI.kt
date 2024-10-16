package com.kotlin.sacalabici.data.network.announcements

import com.kotlin.sacalabici.data.network.AuthInterceptor
import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AnnouncementNetworkModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private fun createOkHttpClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()
    }

    operator fun invoke(token: String?): AnnouncementApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.ANNOUNCEMENT_BASE_URL)
            .client(createOkHttpClient(token))
            .addConverterFactory(gsonFactory)
            .build()
            .create(AnnouncementApiService::class.java)
    }
}