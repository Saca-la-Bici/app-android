package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AnnouncementApiService {
    @GET("anuncios/consultar")
    suspend fun getAnnouncementList(): List<AnnouncementBase>

    @POST("anuncios/registrar")
    suspend fun postAnnouncement(@Body announcement: Announcement): Announcement
}
