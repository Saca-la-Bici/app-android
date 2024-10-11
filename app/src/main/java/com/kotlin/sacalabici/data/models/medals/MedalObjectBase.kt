package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

data class MedalObjectBase(
    @SerializedName("medallasActivas") val medals: ArrayList<MedalBase>
)
