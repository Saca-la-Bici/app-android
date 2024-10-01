package com.kotlin.sacalabici.data.repositories.activities

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada

class PostActivitiesRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? =
        apiActivities.postActivityTaller(taller)

    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? =
        apiActivities.postActivityEvento(evento)

    suspend fun postActivityRodada(rodada: Rodada): Rodada? =
        apiActivities.postActivityRodada(rodada)
}