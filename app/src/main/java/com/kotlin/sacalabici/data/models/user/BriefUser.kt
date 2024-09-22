package com.kotlin.sacalabici.data.models.user

import com.google.gson.annotations.SerializedName

data class BriefUser(
    @SerializedName("username") val username: String,
    @SerializedName("correoElectronico") val correoElectronico: String,
    // Agrega otros campos si es necesario
)