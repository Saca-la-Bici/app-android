package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.repositories.ActivitiesRepository

class PostActivityRequirement {
    private val repository = ActivitiesRepository()

    suspend fun postActivityTaller(taller: ActivityModel): ActivityModel? =
        repository.postActivityTaller(taller)

    suspend fun postActivityEvento(evento: ActivityModel): ActivityModel? =
        repository.postActivityEvento(evento)
}