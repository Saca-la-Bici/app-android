package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

// Reconsiderar el MER*
data class Medal(
    @SerializedName("IDMedalla") val IDMedalla: Int,
    @SerializedName("Nombre") val Nombre: String,
    @SerializedName("Imagen") val Imagen: String
)