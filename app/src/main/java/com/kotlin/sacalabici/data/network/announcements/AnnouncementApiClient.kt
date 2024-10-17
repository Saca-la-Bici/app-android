package com.kotlin.sacalabici.data.network.announcements

import android.content.Context
import android.net.Uri
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.MultipartManager
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

// Clase que maneja las operaciones de la API relacionadas con anuncios
class AnnouncementApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    // Variable para almacenar la instancia de AnnouncementApiService
    private lateinit var api: AnnouncementApiService
    // Instancia de MultipartManager para manejar archivos multipart
    private val multipartManager = MultipartManager()

    // Método suspendido para obtener la lista de anuncios
    suspend fun getAnnouncementList(): AnnouncementObjectBase? {
        // Obtener el token de forma sincrónica
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        api = AnnouncementNetworkModuleDI(token)

        return try {
            api.getAnnouncementList()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    // Método suspendido para eliminar un anuncio por su ID
    suspend fun deleteAnnouncement(id: String): Boolean {
        val token: String?
        try{
            // Obtener el token de forma sincrónica
            token = firebaseTokenManager.getTokenSynchronously()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        api = AnnouncementNetworkModuleDI(token)
        return try {
            api.deleteAnnouncement(id).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun postAnnouncement(announcement: Announcement, context: Context): Announcement? {
        // Obtener el token de forma sincrónica
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)

        // Crear los cuerpos de la solicitud para el título y contenido del anuncio
        val titulo = announcement.titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val contenido = announcement.contenido.toRequestBody("text/plain".toMediaTypeOrNull())

        // Manejar la imagen del anuncio, si existe
        val file = announcement.imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return try {
            api.postAnnouncement(titulo, contenido, img)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun patchAnnouncement(id: String, announcement: Announcement, context: Context): Announcement? {
        // Obtener el token de forma sincrónica
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)

        // Crear los cuerpos de la solicitud para el título y contenido del anuncio
        val titulo = announcement.titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val contenido = announcement.contenido.toRequestBody("text/plain".toMediaTypeOrNull())

        // Manejar la imagen del anuncio, si existe
        val file = announcement.imagen?.let { multipartManager.uriToFile(context, it) }
        val img = file?.let { multipartManager.prepareFilePart("file", Uri.fromFile(it)) }

        return try {
            api.patchAnnouncement(id, titulo, contenido, img)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
