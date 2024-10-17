package com.kotlin.sacalabici.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.activities.ActivitiesBase
import com.kotlin.sacalabici.data.models.activities.ActivityBase
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.profile.ProfileApiClient
import com.kotlin.sacalabici.data.models.profile.Profile

class   ProfileRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiProfile = ProfileApiClient(firebaseTokenManager)

    suspend fun getUsuario(): ProfileBase?{
        return apiProfile.getUsuario()
    }
    suspend fun patchProfile(profile: Profile, context: Context): Profile?{ // Manda a llamar el api request para modificar perfil
        return apiProfile.patchProfile(profile, context)
    }
    suspend fun getActividades(): List<ActivityBase> {
        val activitiesBase: ActivitiesBase? = apiProfile.getActividades()
        val listActivities = mutableListOf<ActivityBase>()
        activitiesBase?.activities?.forEach { itemActivity ->
            val activityId = itemActivity.id

            itemActivity.actividades.forEach{ activity ->
                val updatedActivity = activity.copy(id = activityId)
                listActivities.add(updatedActivity)
            }
        }
        return listActivities
    }

    suspend fun deleteProfile(): Boolean{ // Manda a llamar el api request para eliminar cuenta
        return apiProfile.deleteProfile()
    }

}
