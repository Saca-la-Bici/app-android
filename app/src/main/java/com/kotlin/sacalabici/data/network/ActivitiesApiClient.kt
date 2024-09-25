package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.ActivityBase

class ActivitiesApiClient {
    private val api: ActivitiesApiService = ActivitiesNetworkModuleDI()

    suspend fun getRodadas(): List<ActivityBase> {
        return try {
            api.getRodadas()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getEventos(): List<ActivityBase> {
        return try {
            api.getEventos()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTalleres(): List<ActivityBase> {
        return try {
            api.getTalleres()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}