package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class ConsultarUsuariosBase(
    @SerializedName("usuario") val usuario: Usuario,
    @SerializedName("rol") val rol: Rol
)

data class Usuario(
    @SerializedName("username") val username: String,
    @SerializedName("correoElectronico") val correoElectronico: String,
    // Agrega otros campos si es necesario
)

data class Rol(
    @SerializedName("nombreRol") val nombreRol: String
)
