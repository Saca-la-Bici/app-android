package com.kotlin.sacalabici.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.data.network.profile.ProfileApiClient
import com.kotlin.sacalabici.data.models.profile.Profile

class ProfileRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiProfile = ProfileApiClient(firebaseTokenManager)
    suspend fun getUsuario(): ProfileBase?{
        return apiProfile.getUsuario()
    }
    suspend fun patchProfile(profile: Profile, context: Context): Profile?{
        return apiProfile.patchProfile(profile, context)
    }

}
