package com.kotlin.sacalabici.data.models.routes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Route(
    val titulo: String,
    val distancia: String,
    val tiempo: String,
    val nivel: String,
    val coordenadas: ArrayList<CoordenatesBase>
) : Parcelable
