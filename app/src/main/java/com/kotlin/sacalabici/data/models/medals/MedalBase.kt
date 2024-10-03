package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

data class MedalBase(
    @SerializedName("_id") val id: String,
    @SerializedName("nombre") val name: String,
    @SerializedName("imagen") val url: String
)