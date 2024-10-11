package com.kotlin.sacalabici.data.network.announcements

import android.content.Context
import android.net.Uri
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.MultipartManager
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class AnnouncementApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    private lateinit var api: AnnouncementApiService
    private val multipartManager = MultipartManager()

    suspend fun getAnnouncementList(): AnnouncementObjectBase? {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        api = AnnouncementNetworkModuleDI(token)

        return try {
            api.getAnnouncementList()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteAnnouncement(id: String): Boolean {
        val token: String?
        try{
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
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)

        val titulo = announcement.titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val contenido = announcement.contenido.toRequestBody("text/plain".toMediaTypeOrNull())

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
        val token = firebaseTokenManager.getTokenSynchronously()
        api = AnnouncementNetworkModuleDI(token)

        val titulo = announcement.titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val contenido = announcement.contenido.toRequestBody("text/plain".toMediaTypeOrNull())

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
