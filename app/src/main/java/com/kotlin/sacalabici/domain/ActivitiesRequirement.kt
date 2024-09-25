package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.ActivityBase
import com.kotlin.sacalabici.data.repositories.ActivitiesRepository

class GetRodadasRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<ActivityBase> = repository.getRodadas()
}

class GetEventosRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<ActivityBase> = repository.getEventos()
}

class GetTalleresRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(): List<ActivityBase> = repository.getTalleres()
}