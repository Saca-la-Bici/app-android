package com.kotlin.sacalabici.data.network.activities

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.EventosBase
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
