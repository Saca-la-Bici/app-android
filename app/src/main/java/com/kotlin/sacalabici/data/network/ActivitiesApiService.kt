package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.ActivityModel
import retrofit2.http.Body
import retrofit2.http.POST

interface ActivitiesApiService {
    @POST("actividades/registrar/taller")
        suspend fun postActivityTaller(@Body taller: ActivityModel): ActivityModel

    @POST("actividades/registrar/evento")
        suspend fun postActivityEvento(@Body evento: ActivityModel): ActivityModel
}