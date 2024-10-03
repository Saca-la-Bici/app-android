package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.JoinActivityRequest
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

    suspend fun PostJoinActivity(actividadId:String, tipo:String){
        val token = firebaseTokenManager.getTokenSynchronously()

        if (token != null) {
            Log.d("ActivitiesApiClient", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo, Token: $token")

            api = ActivitiesNetworkModuleDI(token)
            try {
                Log.d("ActivitiesApiClient2", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo, Token: $token")
                val request = JoinActivityRequest(actividadId, tipo)
                api.PostJoinActivity(request)
                Log.d("ActivitiesApiClient3", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo, Token: $token")

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
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
}
