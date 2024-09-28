package com.kotlin.sacalabici.data.models.user

data class User(
    val username: String = "none",
    val nombre: String = "none",
    val fechaNacimiento: String = "none",
    val tipoSangre: String = "none",
    val correoElectronico: String,
    val numeroEmergencia: String = "none",
    val firebaseUID: String = "none"
)