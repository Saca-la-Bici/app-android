package com.kotlin.sacalabici.domain.activities

import android.content.Context
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.data.repositories.activities.PostActivitiesRepository

class PostActivityRequirement {
    private val repository = PostActivitiesRepository()

    suspend fun postActivityTaller(taller: ActivityModel, context: Context): ActivityModel? =
        repository.postActivityTaller(taller, context)

    suspend fun postActivityEvento(evento: ActivityModel, context: Context): ActivityModel? =
        repository.postActivityEvento(evento, context)

    suspend fun postActivityRodada(rodada: Rodada, context: Context): Rodada? =
        repository.postActivityRodada(rodada, context)
}