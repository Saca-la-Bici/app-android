package com.kotlin.sacalabici.data.repositories.medals

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.medals.MedalObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.medals.MedalsApiClient

class MedalsRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiMedals = MedalsApiClient(firebaseTokenManager)

    suspend fun getMedalsList(): MedalObjectBase? =
        try{
            // Realizar la consulta
            val response = apiMedals.getMedalsList()
            response
        } catch (e: Exception){
            Log.e("Falla en getMedalsList", "Error en la consulta de getMedalsList: ${e.message}")
            null
        }
}