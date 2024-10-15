package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Clase de datos que representa información básica sobre una pregunta frecuente
data class FAQBase(
    @SerializedName("_id") val id: String,
    @SerializedName("IdPregunta") val IdPregunta: Int,
    @SerializedName("Pregunta") var Pregunta: String,
    @SerializedName("Respuesta") var Respuesta: String,
    @SerializedName("Tema") val Tema: String,
    @SerializedName("Imagen") val Imagen: String?,
) : Serializable
