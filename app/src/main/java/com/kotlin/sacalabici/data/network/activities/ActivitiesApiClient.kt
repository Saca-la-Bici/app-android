package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.OneActivityBase
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class ActivitiesApiClient(private val firebaseTokenManager: FirebaseTokenManager) {
    private lateinit var api: ActivitiesApiService

    suspend fun getRodadas(): RodadasBase? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getRodadas()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun getEventos(): EventosBase? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getEventos()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun getTalleres(): TalleresBase? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getTalleres()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun getActivityById(id: String): OneActivityBase? {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                val response = api.getActivityById(id)
                Log.d("ApiClient", "Respuesta de la API: $response")
                response
            } catch (e: Exception) {
                Log.e("ApiClient", "Error al obtener la actividad", e)
                null
            }
        } else {
            Log.e("ApiClient", "Token es null")
            null
        }
    }

    suspend fun getPermissions(): PermissionsObject? {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getPermissions()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun postLocation(id: String, location: LocationR): Boolean {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                val response = api.postLocation(id,location)
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    suspend fun getRodadaInfo(id: String): RodadaInfoBase? {
        val token = firebaseTokenManager.getTokenSynchronously()
        Log.d("ActivitiesRepository", "Token retrieved: $token")

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                Log.d("ActivitiesRepository", "Calling API to get Rodada info with id: $id")
                api.getRodadaInfo(id)
            } catch (e: Exception) {
                Log.e("ActivitiesRepository", "Error fetching Rodada info: ${e.message}")
                e.printStackTrace()
                null
            }
        } else {
            Log.w("ActivitiesRepository", "Token is null, cannot proceed")
            null
        }
    }

    suspend fun getUbicacion(id: String): List<LocationR>? {
        val token = firebaseTokenManager.getTokenSynchronously()
        Log.d("ActivitiesRepository", "Token retrieved: $token")

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                Log.d("ActivitiesRepository", "Calling API to get Ubicacion info with id: $id")
                api.getUbicacion(id)
            } catch (e: Exception) {
                Log.e("ActivitiesRepository", "Error fetching Ubicacion info: ${e.message}")
                e.printStackTrace()
                null
            }
        } else {
            Log.w("ActivitiesRepository", "Token is null, cannot proceed")
            null
        }
    }



}
