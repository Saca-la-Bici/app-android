package com.kotlin.sacalabici.data.models.activities

import com.google.gson.annotations.SerializedName

data class RodadaInfoBase(
    @SerializedName("rodadaId") var rodadaId: String,
    @SerializedName("rutaId") var rutaId: String
)