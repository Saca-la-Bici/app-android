package com.kotlin.sacalabici.data.repositories.activities

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada

class PostActivitiesRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun postActivityTaller(taller: ActivityModel, context: Context): ActivityModel? =
        apiActivities.postActivityTaller(taller, context)

    suspend fun postActivityEvento(evento: ActivityModel, context: Context): ActivityModel? =
        apiActivities.postActivityEvento(evento, context)

    suspend fun postActivityRodada(rodada: Rodada, context: Context): Rodada? =
        apiActivities.postActivityRodada(rodada, context)
}