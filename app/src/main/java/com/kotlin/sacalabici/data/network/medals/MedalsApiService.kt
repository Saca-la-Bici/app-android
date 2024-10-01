package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.models.medals.MedalBase
import retrofit2.http.GET

interface MedalsApiService {
    @GET("perfil/consultarMedallas")
    suspend fun getMedalsList(): List<MedalBase>
}