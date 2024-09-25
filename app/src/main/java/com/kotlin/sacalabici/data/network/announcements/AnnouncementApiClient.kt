package com.kotlin.sacalabici.data.network.announcements

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement

class AnnouncementApiClient(private val firebaseTokenManager: FirebaseTokenManager) {
    private lateinit var api: AnnouncementApiService

    suspend fun getAnnouncementList(): List<AnnouncementBase>{
        val token = firebaseTokenManager.returnToken()
        Log.d("token", "Token: $token")
        api = AnnouncementNetworkModuleDI(token)
        return try{
            api.getAnnouncementList()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteAnnouncement(id: String): Boolean {
        val token = firebaseTokenManager.returnToken()
        api = AnnouncementNetworkModuleDI(token)
        return try{
            api.deleteAnnouncement(id).isSuccessful
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun postAnnouncement(announcement: Announcement): Announcement? {
        val token = firebaseTokenManager.returnToken()
        api = AnnouncementNetworkModuleDI(token)
        return try{
            api.postAnnouncement(announcement)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            null
        }
    }
}