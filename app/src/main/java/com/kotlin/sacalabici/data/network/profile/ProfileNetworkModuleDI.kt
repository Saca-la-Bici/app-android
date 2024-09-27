package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.network.profile.ProfileApiService
import com.kotlin.sacalabici.data.network.model.profile.Profile
import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProfileNetworkModuleDI{
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    operator fun invoke(): ProfileApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.ANNOUNCEMENT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonFactory)
            .build()
            .create(ProfileApiService::class.java)
    }
}
