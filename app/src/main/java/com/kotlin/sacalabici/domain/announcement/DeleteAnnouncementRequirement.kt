package com.kotlin.sacalabici.domain.announcement

import com.kotlin.sacalabici.data.repositories.announcement.AnnouncementRepository

class DeleteAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
        id: String
    ): Boolean {
        return repository.deleteAnnouncement(id)
    }

}