package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class GetRodadasRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<Activity> = repository.getRodadas()
}

class GetEventosRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<Activity> = repository.getEventos()
}

class GetTalleresRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<Activity> = repository.getTalleres()
}

class GetActivityByIdRequirement {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(id: String): Activity? = repository.getActivityById(id)
}