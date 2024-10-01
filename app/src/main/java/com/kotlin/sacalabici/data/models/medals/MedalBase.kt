package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

data class MedalBase(
    @SerializedName("nombre") val name: String,
    @SerializedName("imagen") val image: String
)