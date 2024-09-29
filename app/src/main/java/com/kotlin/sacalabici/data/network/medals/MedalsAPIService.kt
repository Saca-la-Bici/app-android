package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.models.medals.Medal
import retrofit2.http.GET

interface MedalsAPIService {
    @GET("consultarMedallas")
    suspend fun consultarMedallasList(): List<Medal>
}
