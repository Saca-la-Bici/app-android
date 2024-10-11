package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class RodadaInfoRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(id: String): RodadaInfoBase? {
        return repository.getInfoRodada(id)
    }
}