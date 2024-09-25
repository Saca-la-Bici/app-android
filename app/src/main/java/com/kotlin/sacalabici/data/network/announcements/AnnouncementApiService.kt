package com.kotlin.sacalabici.data.network.announcements

import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface AnnouncementApiService {
    @GET("anuncios/consultar")
    suspend fun getAnnouncementList(): List<AnnouncementBase>

    @DELETE("anuncios/eliminar/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: String
    ): Response<Void>

    @POST("anuncios/registrar")
    suspend fun postAnnouncement(@Body announcement: Announcement): Announcement
}
