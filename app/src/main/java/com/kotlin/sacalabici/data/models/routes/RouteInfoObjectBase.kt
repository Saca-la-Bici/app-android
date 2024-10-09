package com.kotlin.sacalabici.data.models.routes

import com.google.gson.annotations.SerializedName

data class RouteInfoObjectBase(
    @SerializedName("ruta") val ruta: RouteBase,
    @SerializedName("permisos") val permisos: List<String>
)