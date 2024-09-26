package com.kotlin.sacalabici.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordenadasBase (
    @SerializedName("latitud") var latitud: Double,
    @SerializedName("longitud") var longitud: Double,
    @SerializedName("tipo") var tipo: String
) : Parcelable