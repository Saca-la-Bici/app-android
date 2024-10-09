package com.kotlin.sacalabici.data.repositories


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.profile.ProfileApiClient

class ProfileRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiProfile = ProfileApiClient(firebaseTokenManager)
    suspend fun getUsuario(): ProfileBase?{
        return apiProfile.getUsuario()
    }

}