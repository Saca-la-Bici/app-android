package com.kotlin.sacalabici.data.models.routes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordenatesBase (
    @SerializedName("latitud") var latitud: Double,
    @SerializedName("longitud") var longitud: Double,
    @SerializedName("tipo") var tipo: String
) : Parcelable