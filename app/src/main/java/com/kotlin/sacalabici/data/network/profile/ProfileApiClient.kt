package com.kotlin.sacalabici.data.network.profile

import com.kotlin.sacalabici.data.network.model.profile.Profile
import com.kotlin.sacalabici.data.network.profile.ProfileNetworkModuleDI
import com.kotlin.sacalabici.data.network.model.ProfileBase


class ProfileApiClient {
    private lateinit var api: ProfileApiService
    suspend fun getUsuario(userId: String): Profile? {
        api = ProfileNetworkModuleDI()
        return try {
            api.getUsuario(userId)
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

}
