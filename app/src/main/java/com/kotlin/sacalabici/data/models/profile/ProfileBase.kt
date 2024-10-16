package com.kotlin.sacalabici.data.models.profile

import com.google.gson.annotations.SerializedName

data class ProfileBase(
    @SerializedName("_id") val id: String,
    @SerializedName("username") val user: String,
    @SerializedName("nombre") val name: String,
    @SerializedName("fechaNacimiento") val birthdate: String,
    @SerializedName("tipoSangre") val bloodtype: String?,
    @SerializedName("correoElectronico") val email: String,
    @SerializedName("numeroEmergencia") val emergencyNumber: String,
    @SerializedName("kilometrosRecorridos") val KmCompleted: Double,
    @SerializedName("tiempoRecorrido") val TimeCompleted: Double,
    @SerializedName("rodadasCompletadas") val activitiesCompleted: Int,
    @SerializedName("firebaseUID") val fireUID: String,
    @SerializedName("fechaRegistro") val date: String,
    @SerializedName("__v") val url: Int,
    @SerializedName("imagen") val pImage: String,
    @SerializedName("kilometrosMes") val kilometrosMes: Int,
)
