package com.kotlin.sacalabici.data.models.profile

import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.data.models.user.Rol

data class ConsultarUsuariosBase(
    @SerializedName("usuario") val usuario: BriefUser,
    @SerializedName("rol") val rol: Rol
)