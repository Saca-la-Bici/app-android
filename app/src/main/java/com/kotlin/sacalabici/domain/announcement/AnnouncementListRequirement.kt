package com.kotlin.sacalabici.domain.announcement

import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.repositories.announcement.AnnouncementRepository

class AnnouncementListRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
    ): List<AnnouncementBase> = repository.getAnnouncementList()

}