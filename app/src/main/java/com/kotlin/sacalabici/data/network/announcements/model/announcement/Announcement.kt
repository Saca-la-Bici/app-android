package com.kotlin.sacalabici.data.network.announcements.model.announcement

import android.net.Uri

data class Announcement(
    val titulo: String,
    val contenido: String,
    val imagen: Uri?
)