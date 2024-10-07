package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.CancelActivityRequest
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

    suspend fun PostJoinActivity(actividadId: String, tipo: String): Pair<Boolean, String> {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            Log.d("ActivitiesApiClient", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo, Token: $token")
            api = ActivitiesNetworkModuleDI(token)
            try {
                val request = JoinActivityRequest(actividadId, tipo)

                // Usa runCatching para manejar el resultado
                val result = runCatching { api.PostJoinActivity(request) }

                result.fold(
                    onSuccess = {
                        Log.d("ActivitiesApiClient", "Inscripción exitosa.")
                        Pair(true, "Te has inscrito a la actividad.")  // Operación exitosa
                    },
                    onFailure = { exception ->
                        Log.e("ActivitiesApiClient", "Error al inscribir actividad: ${exception.message}")
                        Pair(false, "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("ActivitiesApiClient", "Excepción al inscribir actividad", e)
                Pair(false, "Error de red o conexión. Intenta más tarde.")
            }
        } else {
            Log.e("ActivitiesApiClient", "Token no disponible")
            Pair(false, "Error de autenticación. Por favor, inicia sesión.")
        }
    }




    suspend fun PostCancelActivity(actividadId: String, tipo: String): Pair<Boolean, String> {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            Log.d("ActivitiesApiClient", "btnJoin Cancel clicked. Activity ID: $actividadId, Type: $tipo, Token: $token")
            api = ActivitiesNetworkModuleDI(token)
            try {
                val request = CancelActivityRequest(actividadId, tipo)

                // Usa runCatching para manejar el resultado
                val result = runCatching { api.PostCancelActivity(request) }

                result.fold(
                    onSuccess = {
                        Log.d("ActivitiesApiClient", "Cancelación exitosa.")
                        Pair(true, "Has cancelado tu inscripción.")
                    },
                    onFailure = { exception ->
                        Log.e("ActivitiesApiClient", "Error al cancelar actividad: ${exception.message}")
                        Pair(false, "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("ActivitiesApiClient", "Excepción al cancelar actividad", e)
                Pair(false, "Error de red o conexión. Intenta más tarde.")
            }
        } else {
            Log.e("ActivitiesApiClient", "Token no disponible")
            Pair(false, "Error de autenticación. Por favor, inicia sesión.")
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
