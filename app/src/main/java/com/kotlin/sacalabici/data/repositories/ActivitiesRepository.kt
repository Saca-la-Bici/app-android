package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityBase

class ActivitiesRepository {
    private val apiActivities = ActivitiesApiClient()

    suspend fun getRodadas(): List<ActivityBase> = apiActivities.getRodadas()
    suspend fun getEventos(): List<ActivityBase> = apiActivities.getEventos()
    suspend fun getTalleres(): List<ActivityBase> = apiActivities.getTalleres()
}