package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    @SerializedName("acknowledged") val acknowledged: Boolean,
    @SerializedName("deletedCount") val deletedCount: Int
)
