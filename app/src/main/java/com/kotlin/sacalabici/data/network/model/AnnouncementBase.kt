package com.kotlin.sacalabici.data.network.model
import com.google.gson.annotations.SerializedName

data class AnnouncementBase (
    @SerializedName("titulo") val title: String,
    @SerializedName("contenido") val content: String,
    @SerializedName("imagen") val url: String?,
)
