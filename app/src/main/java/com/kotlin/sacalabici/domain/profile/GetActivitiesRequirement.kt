package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.models.activities.ActivityBase
import com.kotlin.sacalabici.data.repositories.ProfileRepository


class GetActivitiesRequirement {
    private val repository = ProfileRepository()

    suspend operator fun invoke(): List<ActivityBase> {
        return try {
            repository.getActividades()
        } catch (e: Exception) {
            // Log the exception
            println("Error fetching activities: ${e.message}")
            emptyList()
        }
    }
}