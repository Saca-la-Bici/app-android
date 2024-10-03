package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.models.medals.MedalObjectBase
import retrofit2.http.GET

interface MedalsApiService {
    // Consulta la lista de medallas
    @GET("perfil/consultarMedallas")
    suspend fun getMedalsList(): MedalObjectBase
}
