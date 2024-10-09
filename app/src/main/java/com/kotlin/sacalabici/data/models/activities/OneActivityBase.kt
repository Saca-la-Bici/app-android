package com.kotlin.sacalabici.data.models.activities

import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase

data class OneActivityBase(
    @SerializedName("actividad") val actividad: ApiActivity
)

data class ApiActivity(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val information: List<Activity>,
    @SerializedName("ruta") val route: RouteBase? = null,
    @SerializedName("ubicacion") val location: List<Location>? = null,
)

