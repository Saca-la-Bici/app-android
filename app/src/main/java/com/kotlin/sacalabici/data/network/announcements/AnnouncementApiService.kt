package com.kotlin.sacalabici.data.network.announcements

import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.squareup.okhttp.RequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Part

interface AnnouncementApiService {
    @GET("anuncios/consultar")
    suspend fun getAnnouncementList(): AnnouncementObjectBase

    @DELETE("anuncios/eliminar/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: String
    ): Response<Void>

    @Multipart
    @POST("anuncios/registrar")
    suspend fun postAnnouncement(
        @Part("titulo") titulo: String,
        @Part("contenido") contenido: String,
        @Part imagen: MultipartBody.Part?
    ): Announcement

    @PATCH("anuncios/modificar/{id}")
    suspend fun  patchAnnouncement(
        @Path("id") id: String,
        @Body announcement: Announcement): Announcement
}
