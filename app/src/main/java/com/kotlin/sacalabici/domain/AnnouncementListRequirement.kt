package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class AnnouncementListRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
    ): List<AnnouncementBase> = repository.getAnnouncementList()

    suspend fun deleteAnnouncement(id: String): Boolean = repository.deleteAnnouncement(id)
}