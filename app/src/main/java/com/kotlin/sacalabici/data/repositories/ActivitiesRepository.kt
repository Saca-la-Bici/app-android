package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityModel

class ActivitiesRepository {
    private val apiActivities = ActivitiesApiClient()

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? =
        apiActivities.postActivityTaller(taller)

    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? =
        apiActivities.postActivityEvento(evento)
}