package com.kotlin.sacalabici.data.network.announcements.model

import com.google.gson.annotations.SerializedName

data class AnnouncementObjectBase (
    @SerializedName("anuncio") val announcements: ArrayList<AnnouncementBase>,
    @SerializedName("rol") val role: String,
)
