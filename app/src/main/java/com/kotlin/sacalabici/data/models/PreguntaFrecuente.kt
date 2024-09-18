package com.kotlin.sacalabici.data.models

data class PreguntaFrecuente(
    val IDPregunta: Int,
    val Pregunta: String,
    val Respuesta: String,
    val Tema: String,
    val Imagen: String?
)