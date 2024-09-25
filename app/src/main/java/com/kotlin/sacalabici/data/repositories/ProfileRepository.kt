package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.profile.ProfileNetworkModuleDI
import com.kotlin.sacalabici.data.network.model.ProfileBase
import com.kotlin.sacalabici.data.network.model.profile.Profile
import com.kotlin.sacalabici.data.network.profile.ProfileApiClient

class ProfileRepository {
    private val apiAnnouncement = ProfileApiClient()
    suspend fun getUsuario(userId: String): Profile? = apiAnnouncement.getUsuario(userId)

}