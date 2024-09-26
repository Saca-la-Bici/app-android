package com.kotlin.sacalabici.domain

import android.util.Log
import com.kotlin.sacalabici.data.repositories.AnnouncementRepository

class DeleteAnnouncementRequirement {
    private val repository = AnnouncementRepository()

    suspend operator fun invoke(
        id: String
    ): Boolean {
        Log.d("delete", "Estamos en el requirement")
        return repository.deleteAnnouncement(id)
    }

}