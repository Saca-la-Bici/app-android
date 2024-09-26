package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.AnnouncementApiClient
import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.model.announcement.Announcement

class AnnouncementRepository() {
    private val apiAnnouncement = AnnouncementApiClient()

    suspend fun getAnnouncementList(): List<AnnouncementBase> = apiAnnouncement.getAnnouncementList()

    suspend fun deleteAnnouncement(id: String): Boolean = apiAnnouncement.deleteAnnouncement(id)

    suspend fun postAnnouncement(announcement: Announcement): Announcement? =
        apiAnnouncement.postAnnouncement(announcement)
}