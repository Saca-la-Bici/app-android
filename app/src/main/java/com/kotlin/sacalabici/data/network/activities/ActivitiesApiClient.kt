package com.kotlin.sacalabici.data.network.activities

import android.util.Log
import com.kotlin.sacalabici.data.models.activities.CancelActivityRequest
import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.JoinActivityRequest
import com.kotlin.sacalabici.data.models.activities.OneActivityBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.data.network.MultipartManager
import com.squareup.okhttp.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ActivitiesApiClient(private val firebaseTokenManager: FirebaseTokenManager) {
    private lateinit var api: ActivitiesApiService
    private val multipartManager = MultipartManager()

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
                response
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun postActivityTaller(taller: ActivityModel, context: Context): ActivityModel? {
        val token = firebaseTokenManager.getTokenSynchronously()

        val informacion = taller.informacion

        val titulo = informacion[0].titulo
        val fecha = informacion[0].fecha
        val hora = informacion[0].hora
        val duracion = informacion[0].duracion
        val ubicacion = informacion[0].ubicacion
        val descripcion = informacion[0].descripcion
        val tipo = informacion[0].tipo

        val file = taller.informacion[0].imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.postActivityTaller(
                    titulo,
                    fecha,
                    hora,
                    duracion,
                    ubicacion,
                    descripcion,
                    tipo,
                    img
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun postActivityEvento(evento: ActivityModel, context: Context): ActivityModel? {
        val token = firebaseTokenManager.getTokenSynchronously()

        val informacion = evento.informacion

        val titulo = informacion[0].titulo
        val fecha = informacion[0].fecha
        val hora = informacion[0].hora
        val duracion = informacion[0].duracion
        val ubicacion = informacion[0].ubicacion
        val descripcion = informacion[0].descripcion
        val tipo = informacion[0].tipo

        val file = evento.informacion[0].imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.postActivityEvento(titulo, fecha, hora, duracion, ubicacion, descripcion, tipo, img)
            } catch (e: java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun postActivityRodada(rodada: Rodada, context: Context): Rodada? {
        val token = firebaseTokenManager.getTokenSynchronously()

        val informacion = rodada.informacion

        val titulo = informacion[0].titulo
        val fecha = informacion[0].fecha
        val hora = informacion[0].hora
        val duracion = informacion[0].duracion
        val ubicacion = informacion[0].ubicacion
        val descripcion = informacion[0].descripcion
        val tipo = informacion[0].tipo
        val ruta = rodada.ruta

        val file = rodada.informacion[0].imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.postActivityRodada(titulo, fecha, hora, duracion, ubicacion, descripcion, tipo, ruta, img)
            } catch (e: java.lang.Exception){
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
