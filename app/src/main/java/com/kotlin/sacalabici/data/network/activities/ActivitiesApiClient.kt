package com.kotlin.sacalabici.data.network.activities

import com.kotlin.sacalabici.data.models.activities.CancelActivityRequest
import android.content.Context
import android.net.Uri
import android.util.Log
import com.kotlin.sacalabici.data.models.activities.AttendanceRequest
import com.kotlin.sacalabici.data.models.activities.EventosBase
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.JoinActivityRequest
import com.kotlin.sacalabici.data.models.activities.OneActivityBase
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.activities.RodadasBase
import com.kotlin.sacalabici.data.models.activities.TalleresBase
import com.kotlin.sacalabici.data.models.profile.PermissionsObject
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.data.network.MultipartManager
import com.kotlin.sacalabici.data.network.announcements.AnnouncementNetworkModuleDI
import com.kotlin.sacalabici.data.network.model.ActivityData
import com.kotlin.sacalabici.data.network.model.DeleteActivityRequest
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
                api.postActivityTaller(titulo, fecha, hora, duracion,
                    ubicacion, descripcion, tipo, img)
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
            api = ActivitiesNetworkModuleDI(token)
            try {
                val request = JoinActivityRequest(actividadId, tipo)

                // Usa runCatching para manejar el resultado
                val result = runCatching { api.PostJoinActivity(request) }

                result.fold(
                    onSuccess = {
                        Pair(true, "Te has inscrito a la actividad.")  // Operación exitosa
                    },
                    onFailure = { exception ->
                        Pair(false, "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Pair(false, "Error de red o conexión. Intenta más tarde.")
            }
        } else {
            Pair(false, "Error de autenticación. Por favor, inicia sesión.")
        }
    }

    suspend fun PostCancelActivity(actividadId: String, tipo: String): Pair<Boolean, String> {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                val request = CancelActivityRequest(actividadId, tipo)

                // Usa runCatching para manejar el resultado
                val result = runCatching { api.PostCancelActivity(request) }

                result.fold(
                    onSuccess = {
                        Pair(true, "Has cancelado tu inscripción.")
                    },
                    onFailure = { exception ->
                        Pair(false, "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Pair(false, "Error de red o conexión. Intenta más tarde.")
            }
        } else {
            Pair(false, "Error de autenticación. Por favor, inicia sesión.")
        }
    }


    suspend fun patchActivityTaller(taller: ActivityData, context: Context): ActivityData? {
        val token = firebaseTokenManager.getTokenSynchronously()

        val id = taller.id.toRequestBody("text/plain".toMediaTypeOrNull())
        val titulo = taller.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val fecha = taller.date.toRequestBody("text/plain".toMediaTypeOrNull())
        val hora = taller.time.toRequestBody("text/plain".toMediaTypeOrNull())
        val duracion = taller.duration.toRequestBody("text/plain".toMediaTypeOrNull())
        val ubicacion = taller.location.toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcion = taller.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val tipo = taller.type.toRequestBody("text/plain".toMediaTypeOrNull())
        val peopleEnrolled = taller.peopleEnrolled.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val state = taller.state.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val foro = taller.foro?.toRequestBody("text/plain".toMediaTypeOrNull())

        val file = taller.imageURL?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        val usuariosInscritos = taller.register?.mapIndexed { index, user ->
            MultipartBody.Part.createFormData("usuariosInscritos[$index]", user)
        }

        Log.d("ActivitiesApiClient", "patchActivityTaller: $usuariosInscritos")
        Log.d("ActivitiesApiClient", "$titulo, $fecha, $hora, $duracion, $ubicacion, $descripcion," +
                "$tipo, $peopleEnrolled, $state, $foro, $img, $usuariosInscritos")

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.patchActivityTaller(id,
                    titulo, fecha, hora, duracion, ubicacion, descripcion,
                    tipo, img, peopleEnrolled, state, foro, usuariosInscritos)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun patchActivityEvento(evento: ActivityData, context: Context): ActivityData? {
        val token = firebaseTokenManager.getTokenSynchronously()

        Log.d("ActivitiesApiClient", "patchActivityEvento: $evento")

        val id = evento.id.toRequestBody("text/plain".toMediaTypeOrNull())
        val titulo = evento.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val fecha = evento.date.toRequestBody("text/plain".toMediaTypeOrNull())
        val hora = evento.time.toRequestBody("text/plain".toMediaTypeOrNull())
        val duracion = evento.duration.toRequestBody("text/plain".toMediaTypeOrNull())
        val ubicacion = evento.location.toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcion = evento.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val tipo = evento.type.toRequestBody("text/plain".toMediaTypeOrNull())
        val peopleEnrolled = evento.peopleEnrolled.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val state = evento.state.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val foro = evento.foro?.toRequestBody("text/plain".toMediaTypeOrNull())

        val file = evento.imageURL?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        // Procesar la lista register como MultipartBody.Part
        val usuariosInscritos = evento.register?.mapIndexed { index, user ->
            MultipartBody.Part.createFormData("usuariosInscritos[$index]", user.toString())
        }


        Log.d("ActivitiesApiClient", "$titulo, $fecha, $hora, $duracion, $ubicacion, $descripcion," +
                "$tipo, $peopleEnrolled, $state, $foro, $img, $usuariosInscritos")
        Log.d("ActivitiesApiClient", "patchActivityEvento: $usuariosInscritos")

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.patchActivityEvento(id, titulo, fecha, hora, duracion, ubicacion,
                    descripcion, tipo, img, peopleEnrolled, state, foro, usuariosInscritos)
            } catch (e: java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun patchActivityRodada(rodada: ActivityData, context: Context): ActivityData? {
        val token = firebaseTokenManager.getTokenSynchronously()

        val id = rodada.id.toRequestBody("text/plain".toMediaTypeOrNull())
        val titulo = rodada.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val fecha = rodada.date.toRequestBody("text/plain".toMediaTypeOrNull())
        val hora = rodada.time.toRequestBody("text/plain".toMediaTypeOrNull())
        val duracion = rodada.duration.toRequestBody("text/plain".toMediaTypeOrNull())
        val ubicacion = rodada.location.toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcion = rodada.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val tipo = rodada.type.toRequestBody("text/plain".toMediaTypeOrNull())
        val peopleEnrolled = rodada.peopleEnrolled.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val state = rodada.state.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val foro = rodada.foro?.toRequestBody("text/plain".toMediaTypeOrNull())
        val ruta = rodada.idRouteBase?.toRequestBody("text/plain".toMediaTypeOrNull())

        val file = rodada.imageURL?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        val usuariosInscritos = rodada.register?.mapIndexed { index, user ->
            MultipartBody.Part.createFormData("usuariosInscritos[$index]",
                user.toRequestBody("text/plain".toMediaTypeOrNull()).toString()
            )
        }

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.patchActivityRodada(id, titulo, fecha, hora, duracion, ubicacion,
                    descripcion, tipo, img, peopleEnrolled, state, foro, usuariosInscritos, ruta)
            } catch (e: java.lang.Exception){
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }


    suspend fun deleteActivity(id: String, tipo: String): Boolean {
        val token: String?
        val act = DeleteActivityRequest(id, tipo)
        try{
            token = firebaseTokenManager.getTokenSynchronously()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        api = ActivitiesNetworkModuleDI(token)
        return try {
            api.deleteActivity(act).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
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

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getRodadaInfo(id)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    suspend fun getUbicacion(id: String): List<LocationR>? {
        val token = firebaseTokenManager.getTokenSynchronously()

        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                api.getUbicacion(id)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }


    suspend fun postValidateAttendance(IDRodada: String, codigo: Int): Pair<Boolean, String> {
        val token = firebaseTokenManager.getTokenSynchronously()
        return if (token != null) {
            api = ActivitiesNetworkModuleDI(token)
            try {
                val request = AttendanceRequest(IDRodada, codigo)

                // Usa runCatching para manejar el resultado
                val result = runCatching { api.PostValidateAttendance(request) }

                result.fold(
                    onSuccess = {
                        Pair(true, "Se ha verificado tu asistencia")
                    },
                    onFailure = { exception ->
                        Pair(false, "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Pair(false, "Error de red o conexión. Intenta más tarde.")
            }
        } else {
            Pair(false, "Error de autenticación. Por favor, inicia sesión.")
        }
    }




}
