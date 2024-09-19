package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class ConsultarUsuariosBase(
    @SerializedName("name") val name: String,
    @SerializedName("img") val img: String
)