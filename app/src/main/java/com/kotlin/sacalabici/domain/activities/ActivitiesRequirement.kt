package com.kotlin.sacalabici.domain.activities

import android.util.Log
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

class PostJoinActivity {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(actividadId:String, tipo:String) {
        Log.d("ActivitiesRequirement", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
        repository.PostJoinActivity(actividadId, tipo)
    }
}

class PostCancelActivity {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(actividadId:String, tipo:String) {
        Log.d("ActivitiesRequirement", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
        repository.PostCancelActivity(actividadId, tipo)
    }
}

