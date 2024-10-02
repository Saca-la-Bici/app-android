package com.kotlin.sacalabici.data.models.preguntasFrecuentes

// Clase de datos que representa una lista de preguntas frecuentes
data class FAQ(
    val IdPregunta: Int,
    val Pregunta: String,
    val Respuesta: String,
    val Tema: String,
    val Imagen: String?,
)
