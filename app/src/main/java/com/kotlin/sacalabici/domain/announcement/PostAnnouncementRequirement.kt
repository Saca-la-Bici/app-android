package com.kotlin.sacalabici.domain.announcement

import android.content.Context
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.data.repositories.announcement.AnnouncementRepository

class PostAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(announcement: Announcement, context: Context): Announcement? =
        repository.postAnnouncement(announcement, context)
}