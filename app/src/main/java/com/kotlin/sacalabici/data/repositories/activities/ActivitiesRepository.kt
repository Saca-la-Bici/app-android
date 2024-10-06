package com.kotlin.sacalabici.data.repositories.activities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.network.activities.ActivitiesApiClient
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.Location
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
            itemActivity.activities.forEach{ activity ->
                val updatedActivity = activity.copy(id = rodadaId, nivel = nivel, distancia = distance)
                listRodadas.add(updatedActivity)
            }
        }
        Log.d("ActivitiesRepository", "Rodadas filtradas: $listRodadas")
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
        Log.d("ActivitiesRepository", "Eventos filtrados: $listEventos")
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
        Log.d("ActivitiesRepository", "Talleres filtrados: $listTalleres")
        return listTalleres
    }

    suspend fun getActivityById(id: String): Activity? {
        Log.d("ActivitiesRepository", "Obteniendo actividad con id: $id")
        val response = apiActivities.getActivityById(id)

        // Log para ver la respuesta recibida del API
        Log.d("ActivitiesRepository", "Respuesta del API: $response")

        val activity = response?.actividad?.information?.firstOrNull()
        val nivel = response?.actividad?.route?.nivel
        val distancia = response?.actividad?.route?.distancia

        return activity?.copy(nivel = nivel, distancia = distancia)
    }

    suspend fun getPermissions(): List<String> {
        val permissionsObject = apiActivities.getPermissions()
        return permissionsObject?.permisos ?: emptyList()
    }

    suspend fun postLocation(location: Location): Boolean {
        return apiActivities.postLocation(location)
    }
}