package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class ConsultarUsuariosObject(
    @SerializedName("usuarios") val usuarios: ArrayList<ConsultarUsuariosBase>
)