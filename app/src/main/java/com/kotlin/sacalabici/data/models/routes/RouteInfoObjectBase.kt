package com.kotlin.sacalabici.data.models.routes

import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.profile.PermissionsObject

data class RouteInfoObjectBase(
    @SerializedName("ruta") val ruta: RouteBase,
    @SerializedName("permisos") val permisos: List<String>
)