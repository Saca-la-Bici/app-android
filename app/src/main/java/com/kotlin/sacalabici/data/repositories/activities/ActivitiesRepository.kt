package com.kotlin.sacalabici.data.repositories.activities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase

class ActivitiesRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiActivities = ActivitiesApiClient(firebaseTokenManager)

    suspend fun getRodadas(): List<Activity> {
        val rodadasBase: RodadasBase? = apiActivities.getRodadas()
        val listRodadas = mutableListOf<Activity>()
        rodadasBase?.rodadas?.forEach { itemActivity ->
            val nivel = itemActivity.route?.nivel
            val distance = itemActivity.route?.distancia
            val rodadaId = itemActivity.id
            val rutaId = itemActivity.route?.id
            itemActivity.activities.forEach { activity ->
                val updatedActivity = activity.copy(id = rodadaId, nivel = nivel, distancia = distance, idRouteBase = rutaId)
                listRodadas.add(updatedActivity)
            }
        }
        return listRodadas
    }

    suspend fun getEventos(): List<Activity> {
        val eventosBase: EventosBase? =  apiActivities.getEventos()
        val listEventos = mutableListOf<Activity>()
        eventosBase?.eventos?.forEach { itemActivity ->
            val eventoId = itemActivity.id
            itemActivity.activities.forEach { activity ->
                val updatedActivity = activity.copy(id = eventoId)
                listEventos.add(updatedActivity)
            }
        }
        return listEventos
    }

    suspend fun getTalleres(): List<Activity> {
        val talleresBase: TalleresBase? = apiActivities.getTalleres()
        val listTalleres = mutableListOf<Activity>()
        talleresBase?.talleres?.forEach { itemActivity ->
            val tallerId = itemActivity.id
            itemActivity.activities.forEach { activity ->
                val updatedActivity = activity.copy(id = tallerId)
                listTalleres.add(updatedActivity)
            }
        }
        return listTalleres
    }

    suspend fun getActivityById(id: String): Activity? {
        val response = apiActivities.getActivityById(id)

        val activity = response?.actividad?.information?.firstOrNull()
        val nivel = response?.actividad?.route?.nivel
        val distancia = response?.actividad?.route?.distancia
        val rutaId = response?.actividad?.route?.id

        val activityResponse = activity?.copy(nivel = nivel, distancia = distancia, idRouteBase = rutaId)
        Log.d("UnaActividad","${activityResponse}")
        return activityResponse
    }

    suspend fun getPermissions(): List<String> {
        val permissionsObject = apiActivities.getPermissions()
        return permissionsObject?.permisos ?: emptyList()
    }

    suspend fun postJoinActivity(actividadId: String, tipo: String): Pair<Boolean, String> {
        return apiActivities.PostJoinActivity(actividadId, tipo)
    }

    suspend fun postCancelActivity(actividadId: String, tipo: String): Pair<Boolean, String> {
        return apiActivities.PostCancelActivity(actividadId, tipo)
    }


}