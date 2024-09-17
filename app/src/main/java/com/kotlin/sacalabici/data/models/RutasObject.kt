package com.kotlin.sacalabici.data.models

import com.google.gson.annotations.SerializedName

data class RutasObject (
    @SerializedName("listaDeRutas") var rutas: ArrayList<RutasBase>,
)