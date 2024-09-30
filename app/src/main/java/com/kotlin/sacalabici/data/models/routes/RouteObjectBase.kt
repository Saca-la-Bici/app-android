package com.kotlin.sacalabici.data.models.routes

import com.google.gson.annotations.SerializedName

data class RouteObjectBase(
    @SerializedName("rutas") val routes: List<RouteBase>,
    @SerializedName("permisos") val permission: List<String>
)
