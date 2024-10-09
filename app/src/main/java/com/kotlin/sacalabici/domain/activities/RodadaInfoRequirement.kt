package com.kotlin.sacalabici.domain.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class RodadaInfoRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(id: String): RodadaInfoBase? {
        Log.d("RodadaInfoRequirement", "Fetching Rodada info with id: $id")
        return repository.getInfoRodada(id)
    }
}