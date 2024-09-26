package com.kotlin.sacalabici.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RutasBase (
    @SerializedName("_id") var id: String,
    @SerializedName("titulo") var titulo: String,
    @SerializedName("distancia") var distancia: String,
    @SerializedName("tiempo") var tiempo: String,
    @SerializedName("nivel") var nivel: String,
    @SerializedName("coordenadas") var coordenadas: ArrayList<CoordenadasBase>,
) : Parcelable
