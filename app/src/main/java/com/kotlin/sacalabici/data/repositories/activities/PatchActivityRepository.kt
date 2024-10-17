package com.kotlin.sacalabici.data.repositories.activities

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.model.ActivityData

class PatchActivityRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun patchActivityTaller(taller: ActivityData, context: Context): ActivityData? =
        apiActivities.patchActivityTaller(taller, context)

    suspend fun patchActivityEvento(evento: ActivityData, context: Context): ActivityData? =
        apiActivities.patchActivityEvento(evento, context)

    suspend fun patchActivityRodada(rodada: ActivityData, context: Context): ActivityData? =
        apiActivities.patchActivityRodada(rodada, context)
}