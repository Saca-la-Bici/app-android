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

class GetActivityByIdRequirement {
    private val repository = ActivitiesRepository()
    suspend operator fun invoke(id: String): Activity? = repository.getActivityById(id)
}

class PostJoinActivity {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(actividadId: String, tipo: String): Pair<Boolean, String> {
        return try {
            Log.d("PostJoinActivity", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
            repository.postJoinActivity(actividadId, tipo)
        } catch (e: Exception) {
            Log.e("PostJoinActivity", "Error al inscribir en la actividad", e)
            Pair(false, "Error al intentar inscribir la actividad. Por favor, intenta más tarde.")
        }
    }
}


class PostCancelActivity {
    private val repository = ActivitiesRepository()

    suspend operator fun invoke(actividadId: String, tipo: String): Pair<Boolean, String> {
        return try {
            Log.d("PostCancelActivity", "btnJoin Cancel clicked. Activity ID: $actividadId, Type: $tipo")
            repository.postCancelActivity(actividadId, tipo)
        } catch (e: Exception) {
            Log.e("PostCancelActivity", "Error al cancelar la actividad", e)
            Pair(false, "Error al intentar cancelar la actividad. Por favor, intenta más tarde.")
        }
    }
}


