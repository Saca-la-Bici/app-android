package com.kotlin.sacalabici.data.models

data class User(
    val username: String = "none",
    val nombre: String = "none",
    val edad: Int = 0,
    val tipoSangre: String = "none",
    val correoElectronico: String,
    val numeroEmergencia: String = "none",
    val firebaseUID: String = "none"
)