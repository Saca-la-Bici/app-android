package com.kotlin.sacalabici.data.models.routes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class RouteObjectBase(
    @SerializedName("rutas") val routes: List<RouteBase>,
    @SerializedName("permisos") val role: List<String>
)
