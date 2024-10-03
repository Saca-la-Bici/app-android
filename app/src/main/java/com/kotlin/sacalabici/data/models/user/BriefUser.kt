package com.kotlin.sacalabici.data.models.user

import com.google.gson.annotations.SerializedName

data class BriefUser(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("correoElectronico") val correoElectronico: String,
    @SerializedName("imagenPerfil") val imagenPerfil: String,
    // Agrega otros campos si es necesario
)