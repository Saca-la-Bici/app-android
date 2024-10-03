package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName

data class FAQObjectBase(
    @SerializedName("code") val code: Int?,
    @SerializedName("msg") val message: String?,
    @SerializedName("permisos") val permissions: ArrayList<String>,
    @SerializedName("data") val faqs: ArrayList<FAQBase>,
)
