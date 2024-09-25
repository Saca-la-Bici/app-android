package com.kotlin.sacalabici.data.network.model

data class Activity(
    val titulo: String,
    val fecha: String,
    val hora: String,
    val personasInscritas: Int,
    val ubicacion: String,
    val duracion: String,
    val imagen: String? = null  // El valor 'null' es opcional ya que la imagen no es obligatoria
)