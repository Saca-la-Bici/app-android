package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.network.AnnouncementApiClient
import com.kotlin.sacalabici.data.network.model.AnnouncementBase

class AnnouncementRepository() {
    private val apiAnnouncement = AnnouncementApiClient()

    suspend fun getAnnouncementList(): List<AnnouncementBase> = apiAnnouncement.getAnnouncementList()

    suspend fun deleteAnnouncement(id: String): Boolean = apiAnnouncement.deleteAnnouncement(id)
}