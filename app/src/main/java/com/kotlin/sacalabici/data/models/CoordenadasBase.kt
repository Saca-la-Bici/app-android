package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class CoordenadasBase (
    @SerializedName("latitud") var latitud: Double,
    @SerializedName("longitud") var longitud: Double,
    @SerializedName("tipo") var tipo: String,
    @SerializedName("_id") var id: String,
)