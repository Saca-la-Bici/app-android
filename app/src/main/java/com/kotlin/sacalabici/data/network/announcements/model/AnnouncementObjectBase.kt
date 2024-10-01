package com.kotlin.sacalabici.data.network.announcements.model

import com.google.gson.annotations.SerializedName

data class AnnouncementObjectBase (
    @SerializedName("announcements") val announcements: ArrayList<AnnouncementBase>,
    @SerializedName("permisos") val permissions: ArrayList<String>,
)
