package com.kotlin.sacalabici.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.announcements.AnnouncementApiClient
import com.kotlin.sacalabici.data.network.announcements.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement

class AnnouncementRepository() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiAnnouncement = AnnouncementApiClient(firebaseTokenManager)

    suspend fun getAnnouncementList(): List<AnnouncementBase> =
        apiAnnouncement.getAnnouncementList()

    suspend fun deleteAnnouncement(id: String): Boolean {
        Log.d("delete", "Estamos en el repository")
        return apiAnnouncement.deleteAnnouncement(id)
    }
    suspend fun postAnnouncement(announcement: Announcement): Announcement? =
        apiAnnouncement.postAnnouncement(announcement)

    suspend fun putAnnouncement(id: String, announcement: Announcement): Announcement? =
        apiAnnouncement.putAnnouncement(id, announcement)
}