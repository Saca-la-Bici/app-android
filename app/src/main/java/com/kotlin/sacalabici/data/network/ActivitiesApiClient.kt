package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.ActivityModel

class ActivitiesApiClient {
    private lateinit var api: ActivitiesApiService
    val networkModule = ActivitiesNetworkModuleDI()

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? {
        api = networkModule.invoke()
        return try{
            api.postActivityTaller(taller)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? {
        api = networkModule.invoke()
        return try {
            api.postActivityEvento(evento)
        } catch (e: java.lang.Exception){
            e.printStackTrace()
            null
        }
    }
}