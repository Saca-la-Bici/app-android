package com.kotlin.sacalabici.data.repositories.medals

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.medals.MedalsApiClient

class MedalsRepository {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiMedals = MedalsApiClient(firebaseTokenManager)

    suspend fun getMedalsList(): List<MedalBase>? =
        apiMedals.getMedalsList()
}