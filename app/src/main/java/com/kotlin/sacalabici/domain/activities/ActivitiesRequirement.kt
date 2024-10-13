package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.LocationR
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

class GetActivityByIdRequirement {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(id: String): Activity? = repository.getActivityById(id)
}

class PostLocationRequirement {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(id: String, location: LocationR): Boolean = repository.postLocation(id, location)
}

class PostJoinActivity {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(actividadId: String, tipo: String): Pair<Boolean, String> {
        return try {
            repository.postJoinActivity(actividadId, tipo)
        } catch (e: Exception) {
            Pair(false, "Error al intentar inscribir la actividad. Por favor, intenta más tarde.")
        }
    }
}


class PostCancelActivity {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(actividadId: String, tipo: String): Pair<Boolean, String> {
        return try {
            repository.postCancelActivity(actividadId, tipo)
        } catch (e: Exception) {
            Pair(false, "Error al intentar cancelar la actividad. Por favor, intenta más tarde.")
        }
    }
}