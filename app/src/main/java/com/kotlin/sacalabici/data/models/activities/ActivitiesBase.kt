package com.kotlin.sacalabici.data.models.activities

import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.routes.Route
import java.util.Date

data class RodadasBase(
    @SerializedName("rodadas") val rodadas: List<RodadaActivity>,
    @SerializedName("rol") val role: String
)

data class EventosBase(
    @SerializedName("eventos") val eventos: List<DefaultActivity>,
    @SerializedName("rol") val role: String
)

data class TalleresBase(
    @SerializedName("talleres") val talleres: List<DefaultActivity>,
    @SerializedName("rol") val role: String
)

data class RodadaActivity(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val activities: List<Activity>,
    @SerializedName("ruta") val route: Route? = null
)

data class DefaultActivity(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val activities: List<Activity>,
)

data class Activity(
    @SerializedName("_id") val id: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("fecha") val date: Date,
    @SerializedName("hora") val time: String,
    @SerializedName("ubicacion") val location: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("duracion") val duration: String,
    @SerializedName("imagen") val imageURL: String? = null,  // El valor 'null' es opcional ya que la imagen no es obligatoria
    @SerializedName("tipo") val type: String,
    @SerializedName("personasInscritas") val peopleEnrolled: Int,
    @SerializedName("estado") val state: Boolean,
    @SerializedName("comentarios") val comments: String? = null
)
