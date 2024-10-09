package com.kotlin.sacalabici.data.network.model

import android.net.Uri
import java.util.Date

// Modelo de datos para la información de la actividad (POST)
data class Informacion(
    val titulo: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val descripcion: String,
    val duracion: String,
    val imagen: Uri?,
    val tipo: String
)

// Modelo de datos para almacenar información de actividad (POST)
data class ActivityInfo(
    val title: String,
    val date: String,
    val hour: String,
    val minutes: String,
    val hourDur: String,
    val minutesDur: String,
    val ubi: String,
    val description: String
)

// Modelo de datos para eventos y rodadas (POST)
data class ActivityModel(
    val informacion: List<Informacion>
)

// Modelo de datos para rodadas (POST)
data class Rodada(
    val informacion: List<Informacion>,
    val ruta: String
)