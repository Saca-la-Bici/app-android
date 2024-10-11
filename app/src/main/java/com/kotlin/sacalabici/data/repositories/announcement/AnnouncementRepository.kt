package com.kotlin.sacalabici.data.repositories.announcement

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.network.announcements.AnnouncementApiClient
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement

class AnnouncementRepository() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiAnnouncement = AnnouncementApiClient(firebaseTokenManager)

    suspend fun getAnnouncementList(): AnnouncementObjectBase? =
        apiAnnouncement.getAnnouncementList()

    suspend fun deleteAnnouncement(id: String): Boolean {
        return apiAnnouncement.deleteAnnouncement(id)
    }
    suspend fun postAnnouncement(announcement: Announcement, context: Context): Announcement? =
        apiAnnouncement.postAnnouncement(announcement, context)

    suspend fun patchAnnouncement(id: String, announcement: Announcement, context: Context): Announcement? =
        apiAnnouncement.patchAnnouncement(id, announcement, context)
}