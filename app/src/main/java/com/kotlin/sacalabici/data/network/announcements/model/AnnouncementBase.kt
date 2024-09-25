package com.kotlin.sacalabici.data.network.announcements.model
import com.google.gson.annotations.SerializedName

data class AnnouncementBase (
    @SerializedName("_id") val id: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("contenido") val content: String,
    @SerializedName("imagen") val url: String?,
)
