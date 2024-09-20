package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class ConsultarUsuariosBase(
    @SerializedName("username") val username: String,
)