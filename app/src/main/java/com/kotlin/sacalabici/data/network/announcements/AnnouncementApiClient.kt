package com.kotlin.sacalabici.data.network.announcements

import android.util.Log
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement

class AnnouncementApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    private lateinit var api: AnnouncementApiService

    suspend fun getAnnouncementList(): List<AnnouncementBase> {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica

        return if (token != null) {
            api = AnnouncementNetworkModuleDI(token)
            try {
                api.getAnnouncementList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun deleteAnnouncement(id: String): Boolean {
        val token: String?
        try{
            token = firebaseTokenManager.getTokenSynchronously()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        api = AnnouncementNetworkModuleDI(token)
        return try {
            api.deleteAnnouncement(id).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun postAnnouncement(announcement: Announcement): Announcement? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)
        return try {
            api.postAnnouncement(announcement)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun putAnnouncement(id: String, announcement: Announcement): Announcement? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)
        return try {
            api.putAnnouncement(id, announcement)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
