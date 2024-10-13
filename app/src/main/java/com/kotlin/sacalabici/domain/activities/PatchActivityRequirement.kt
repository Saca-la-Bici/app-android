package com.kotlin.sacalabici.domain.activities

import android.content.Context
import com.kotlin.sacalabici.data.network.model.ActivityData
import com.kotlin.sacalabici.data.repositories.activities.PatchActivityRepository

class PatchActivityRequirement {
    private val repository = PatchActivityRepository()
    // Modificar actividades por tipo
    suspend fun patchActivityTaller(taller: ActivityData, context: Context): ActivityData? =
        repository.patchActivityTaller(taller, context)

    suspend fun patchActivityEvento(evento: ActivityData, context: Context): ActivityData? =
        repository.patchActivityEvento(evento, context)

    suspend fun patchActivityRodada(rodada: ActivityData, context: Context): ActivityData? =
        repository.patchActivityRodada(rodada, context)
}