package com.kotlin.sacalabici.data.models.session

import com.google.gson.annotations.SerializedName

class GetUsernameObject {
    @SerializedName("usernameExistente") val usernameExistente: Boolean = false
    @SerializedName("mensaje") val message: String = ""
}