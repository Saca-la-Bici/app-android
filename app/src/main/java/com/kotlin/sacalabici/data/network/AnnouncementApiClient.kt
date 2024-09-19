package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.AnnouncementBase

class AnnouncementApiClient {
    private lateinit var api: AnnouncementApiService

    suspend fun getAnnouncementList(): List<AnnouncementBase>{
        api = AnnouncementNetworkModuleDI()
        return try{
            api.getAnnouncementList()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteAnnouncement(id: String): Boolean {
        api = AnnouncementNetworkModuleDI()
        return try{
            api.deleteAnnouncement(id).isSuccessful
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            false
        }
    }
}