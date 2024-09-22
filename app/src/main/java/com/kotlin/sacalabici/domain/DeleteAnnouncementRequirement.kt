package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class DeleteAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
        id: String
    ): Boolean = repository.deleteAnnouncement(id)


}