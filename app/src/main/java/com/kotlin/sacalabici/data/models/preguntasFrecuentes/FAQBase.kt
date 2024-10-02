package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName

// Clase de datos que representa información básica sobre una pregunta frecuente
data class FAQBase(
    @SerializedName("IdPregunta") val IdPregunta: Int,
    @SerializedName("Pregunta") val Pregunta: String,
    @SerializedName("Respuesta") val Respuesta: String,
    @SerializedName("Tema") val Tema: String,
    @SerializedName("Imagen") val Imagen: String?,
)
