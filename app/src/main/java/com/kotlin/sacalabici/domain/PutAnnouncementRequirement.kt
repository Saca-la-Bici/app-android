package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class PutAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(id: String, announcement: Announcement): Announcement? =
        repository.putAnnouncement(id, announcement)
}
