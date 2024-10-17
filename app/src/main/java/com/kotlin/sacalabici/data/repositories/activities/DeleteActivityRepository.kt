package com.kotlin.sacalabici.data.repositories.activities

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient

class DeleteActivityRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun deleteActivity(id: String, typeAct: String): Boolean {
        return apiActivities.deleteActivity(id, typeAct)
    }
}