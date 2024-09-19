package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConsultarUsuariosModuleDI {
    private val gsonFactory:GsonConverterFactory = GsonConverterFactory.create()

    operator fun invoke(): ConsultarUsuariosAPIService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(gsonFactory)
            .build()
            .create(ConsultarUsuariosAPIService::class.java)
    }
}