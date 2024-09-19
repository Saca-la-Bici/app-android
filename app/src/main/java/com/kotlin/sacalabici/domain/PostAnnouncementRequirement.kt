package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class PostAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(announcement: Announcement){
        repository.postAnnouncement(announcement)
    }
}