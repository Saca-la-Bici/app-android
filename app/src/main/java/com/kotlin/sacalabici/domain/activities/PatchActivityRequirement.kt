package com.kotlin.sacalabici.domain.activities

import android.content.Context
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.data.repositories.activities.PatchActivityRepository

class PatchActivityRequirement {
    private val repository = PatchActivityRepository()
    // Modificar actividades por tipo
    suspend fun patchActivityTaller(id: String, taller: ActivityModel, context: Context): ActivityModel? =
        repository.patchActivityTaller(id, taller, context)

    suspend fun patchActivityEvento(id: String, evento: ActivityModel, context: Context): ActivityModel? =
        repository.patchActivityEvento(id, evento, context)

    suspend fun patchActivityRodada(id: String, rodada: Rodada, context: Context): Rodada? =
        repository.patchActivityRodada(id, rodada, context)
}