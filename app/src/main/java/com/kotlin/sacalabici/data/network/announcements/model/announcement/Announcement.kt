package com.kotlin.sacalabici.data.network.announcements.model.announcement

data class Announcement(
    val IDUsuario: Int,
    val titulo: String,
    val contenido: String,
    val imagen: String?
)