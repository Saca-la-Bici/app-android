package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada

class ActivitiesApiClient(private val firebaseTokenManager: FirebaseTokenManager) {
    private lateinit var api: ActivitiesApiService
    private val networkModule = ActivitiesNetworkModuleDI()

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = networkModule.invoke(token)
            try {
                api.postActivityTaller(taller)
            } catch (e:java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }


    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = networkModule.invoke(token)
            try {
                api.postActivityEvento(evento)
            } catch (e: java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun postActivityRodada(rodada: Rodada): Rodada? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = networkModule.invoke(token)
            try {
                api.postActivityRodada(rodada)
            } catch (e: java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}