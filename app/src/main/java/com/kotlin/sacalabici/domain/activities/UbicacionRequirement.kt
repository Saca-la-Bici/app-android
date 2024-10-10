package com.kotlin.sacalabici.domain.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class UbicacionRequirement {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(id: String): List<LocationR>? {
        Log.d("RodadaInfoRequirement", "Fetching Rodada info with id: $id")
        return repository.getUbicacion(id)
    }
}