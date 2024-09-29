package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName

/*
data class PreguntaFrecuente(
    @SerializedName("IdPregunta") val IdPregunta: Int,
    @SerializedName("Pregunta") val Pregunta: String,
    @SerializedName("Respuesta") val Respuesta: String,
    @SerializedName("Tema") val Tema: String,
    @SerializedName("Imagen") val Imagen: String
)
 */

data class PreguntaFrecuente(
    val IdPregunta: Int,
    val Pregunta: String,
    val Respuesta: String,
    val Tema: String,
    val Imagen: String
)