package com.kotlin.sacalabici.data.models.profile

import com.google.gson.annotations.SerializedName

data class ConsultarUsuariosObject(
    @SerializedName("usuarios") val usuarios: ArrayList<ConsultarUsuariosBase>
)