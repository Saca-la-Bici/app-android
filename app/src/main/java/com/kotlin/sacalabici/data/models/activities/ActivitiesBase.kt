package com.kotlin.sacalabici.data.models.activities

import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.routes.RouteBase
import java.util.Date

data class RodadasBase(
    @SerializedName("rodadas") val rodadas: List<RodadaActivity>,
    @SerializedName("permisos") val permisos: List<String>
)

data class EventosBase(
    @SerializedName("eventos") val eventos: List<DefaultActivity>,
    @SerializedName("permisos") val permisos: List<String>
)

data class TalleresBase(
    @SerializedName("talleres") val talleres: List<DefaultActivity>,
    @SerializedName("permisos") val permisos: List<String>
)

data class RodadaActivity(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val activities: List<Activity>,
    @SerializedName("ruta") val route: RouteBase? = null,
    @SerializedName("ubicacion") val liveLocation: List<Location>
)

data class DefaultActivity(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val activities: List<Activity>,
)

data class Activity(
    @SerializedName("_id") var id: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("fecha") val date: Date,
    @SerializedName("hora") val time: String,
    @SerializedName("ubicacion") val location: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("duracion") val duration: String,
    @SerializedName("imagen") val imageURL: String? = null,
    @SerializedName("tipo") val type: String,
    @SerializedName("personasInscritas") val peopleEnrolled: Int,
    @SerializedName("estado") val state: Boolean,
    @SerializedName("foro") val foro: String? = null,
    @SerializedName("usuariosInscritos") val register: List<String>? = null,
    // Elementos específicos de rodada
    @SerializedName("nivel") val nivel: String? = null,
    @SerializedName("distancia") val distancia: String? = null,
    @SerializedName("id") val idRouteBase: String? = null
)


data class Location(
    @SerializedName("latitud") val latitude: Double,
    @SerializedName("longitud") val longitude: Double
)
data class JoinActivityRequest(
    val actividadId: String,
    val tipo: String
)

data class CancelActivityRequest(
    val actividadId: String,
    val tipo: String
)