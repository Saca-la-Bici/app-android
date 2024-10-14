package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class DeleteLocationRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(id: String): RouteBase? {
        return repository.deleteLocation(id)
    }
}