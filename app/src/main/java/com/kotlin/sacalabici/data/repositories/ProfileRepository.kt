package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.model.ProfileBase
import com.kotlin.sacalabici.data.network.profile.ProfileApiClient

class ProfileRepository {
    private val apiProfile = ProfileApiClient()
    suspend fun getUsuario(userId: String): ProfileBase? = apiProfile.getUsuario(userId)

}