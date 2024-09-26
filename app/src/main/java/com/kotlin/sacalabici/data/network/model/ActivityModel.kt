package com.kotlin.sacalabici.data.network.model

import java.util.Date

data class Informacion(
    val titulo: String,
    val fecha: Date,
    val hora: String,
    val ubicacion: String,
    val descripcion: String,
    val duracion: String,
    val imagen: String? = null,  // El valor 'null' es opcional ya que la imagen no es obligatoria
    val tipo: String
)

data class ActivityModel(
    val informacion: List<Informacion>
)

data class Rodada(
    val informacion: List<Informacion>,
    val ruta: String
)