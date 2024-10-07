package com.kotlin.sacalabici.data.models.profile

import android.net.Uri

data class Profile(
    val username: String,
    val nombre: String,
    val tipoSangre: String,
    val numeroEmergencia: String,
    val rodadasCompletadas: Int,
    val tiempoRecorrido: Int,
    val kilometrosRecorridos: Double,
    val imagen: Uri?
)

