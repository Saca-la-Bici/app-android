package com.kotlin.sacalabici.data.network

import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import retrofit2.http.GET

interface AnnouncementApiService {
    @GET("consultar")
    suspend fun getAnnouncementList(): List<AnnouncementBase>
}