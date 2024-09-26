package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class PatchAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(id: String, announcement: Announcement): Announcement? =
        repository.putAnnouncement(id, announcement)
}
