package com.kotlin.sacalabici.domain.announcement

import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.data.repositories.announcement.AnnouncementRepository

class AnnouncementListRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
    ): AnnouncementObjectBase? = repository.getAnnouncementList()

}