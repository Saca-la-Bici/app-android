package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class RutasBase (
    @SerializedName("_id") var id: String,
    @SerializedName("titulo") var titulo: String,
    @SerializedName("distancia") var distancia: String,
    @SerializedName("tiempo") var tiempo: String,
    @SerializedName("nivel") var nivel: String,
    @SerializedName("lugar") var lugar: String,
    @SerializedName("descanso") var descanso: String,
    @SerializedName("coordenadas") var coordenadas: ArrayList<CoordenadasBase>,
)
