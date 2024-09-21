package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface AnnouncementApiService {
    @GET("anuncios/consultar")
    suspend fun getAnnouncementList(): List<AnnouncementBase>

    @DELETE("anuncios/eliminar/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: String
    ): Response<Void>

    @POST("anuncios/registrar")
    suspend fun postAnnouncement(@Body announcement: Announcement): Announcement

    @PUT("anuncios/modificar/{id}")
    suspend fun  putAnnouncement(
        @Path("id") id: String,
        @Body announcement: Announcement): Announcement
}
