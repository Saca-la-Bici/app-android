package com.kotlin.sacalabici.data.models.medals

import com.google.gson.annotations.SerializedName

data class MedalObjectBase(
    @SerializedName("code") val code: Int?,
    @SerializedName("msg") val message: String?,
    @SerializedName("permisos") val permissions: ArrayList<String>,
    @SerializedName("data") val medals: ArrayList<MedalBase>
)
