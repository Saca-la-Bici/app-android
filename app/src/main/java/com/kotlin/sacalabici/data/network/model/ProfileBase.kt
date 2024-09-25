package com.kotlin.sacalabici.data.network.model

import com.google.gson.annotations.SerializedName

data class ProfileBase(
    @SerializedName("username") val user: String,
    @SerializedName("nombre") val name: String,
    @SerializedName("tipoSangre") val bloodtype: String?,
    @SerializedName("numeroEmergencia") val emergencyNumber: String,
    @SerializedName("rodadasCompletadas") val activitiesCompleted: Int,
    @SerializedName("tiempoRecorrido") val TimeCompleted: Int,
    @SerializedName("kilometrosRecorridos") val KmCompleted: Int,
    @SerializedName("imagen") val url: String?
)
