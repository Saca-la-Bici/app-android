package com.kotlin.sacalabici.data.models.profile

import com.google.gson.annotations.SerializedName

data class PermissionsObject (
    @SerializedName("permisos") val permisos: List<String>
)