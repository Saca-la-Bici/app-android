package com.kotlin.sacalabici.data.network.model

import com.google.gson.annotations.SerializedName

data class ProfileBase(
    @SerializedName("_id") val id: String,
    @SerializedName("username") val user: String,
    @SerializedName("nombre") val name: String,
    @SerializedName("fechaNacimiento") val birthdate: String,
    @SerializedName("tipoSangre") val bloodtype: String?,
    @SerializedName("correoElectronico") val email: String,
    @SerializedName("numeroEmergencia") val emergencyNumber: String,
    @SerializedName("kilometrosRecorridos") val KmCompleted: Int,
    @SerializedName("tiempoRecorrido") val TimeCompleted: Int,
    @SerializedName("rodadasCompletadas") val activitiesCompleted: Int,
    @SerializedName("firebaseUID") val fireUID: String,
    @SerializedName("_v") val url:Int
)
{
    fun printProfileDetails() {
        println("User: $user")
        println("Name: $name")
        println("Birthdate: $birthdate")
        println("Blood Type: $bloodtype")
        println("Email: $email")
        println("Emergency Number: $emergencyNumber")
        println("Kilometers Completed: $KmCompleted")
        println("Time Completed: $TimeCompleted")
        println("Activities Completed: $activitiesCompleted")
        println("Firebase UID: $fireUID")
        println("URL: $url")
    }
}