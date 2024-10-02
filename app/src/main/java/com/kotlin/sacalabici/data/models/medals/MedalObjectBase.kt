package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

data class MedalObjectBase(
    @SerializedName("Medallas") val medals: ArrayList<MedalBase>
)
