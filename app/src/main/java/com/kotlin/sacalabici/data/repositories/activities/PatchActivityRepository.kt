package com.kotlin.sacalabici.data.repositories.activities

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada

class PatchActivityRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun patchActivityTaller(id: String, taller: ActivityModel, context: Context): ActivityModel? =
        apiActivities.patchActivityTaller(id, taller, context)

    suspend fun patchActivityEvento(id: String, evento: ActivityModel, context: Context): ActivityModel? =
        apiActivities.patchActivityEvento(id, evento, context)

    suspend fun patchActivityRodada(id: String, rodada: Rodada, context: Context): Rodada? =
        apiActivities.patchActivityRodada(id, rodada, context)
}