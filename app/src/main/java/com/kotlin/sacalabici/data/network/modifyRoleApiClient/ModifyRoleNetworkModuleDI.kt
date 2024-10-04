package com.kotlin.sacalabici.data.network.modificateRole

import com.kotlin.sacalabici.data.network.AuthInterceptor
import com.kotlin.sacalabici.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

object ModifyRoleNetworkModuleDI {
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()

    private fun createOkHttpClient(token: String?): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

    operator fun invoke(token: String?): ModifyRoleAPIService =
        retrofit2.Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(gsonFactory)
            .client(createOkHttpClient(token))
            .build()
            .create(ModifyRoleAPIService::class.java)
}