package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.network.model.ProfileBase

class ProfileApiClient {
    private val api: ProfileApiService = ProfileNetworkModuleDI()

    suspend fun getUsuario(userId: String): ProfileBase? {
        return try {
            api.getUsuario(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

