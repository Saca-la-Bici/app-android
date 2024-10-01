package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.data.repositories.activities.PostActivitiesRepository

class PostActivityRequirement {
    private val repository = PostActivitiesRepository()

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? =
        repository.postActivityTaller(taller)

    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? =
        repository.postActivityEvento(evento)

    suspend fun postActivityRodada(rodada: Rodada): Rodada? =
        repository.postActivityRodada(rodada)
}