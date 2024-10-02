package com.kotlin.sacalabici.domain.announcement

import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.data.repositories.announcement.AnnouncementRepository

class PatchAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(id: String, announcement: Announcement): Announcement? =
        repository.patchAnnouncement(id, announcement)
}
