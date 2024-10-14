package com.kotlin.sacalabici.data.models.activities
import com.google.gson.annotations.SerializedName
import com.kotlin.sacalabici.data.models.routes.Route

data class ActivityBase(
    @SerializedName("_id") var id: String,
    @SerializedName("titulo") val title: String,
    @SerializedName("fecha") val date: String?= null,
    @SerializedName("hora") val time: String,
    @SerializedName("personasInscritas") val peopleEnrolled: Int,
    @SerializedName("estado") val state: Boolean,
    @SerializedName("ubicacion") val location: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("duracion") val duration: String,
    @SerializedName("imagen") val imageURL: String? = null,  // El valor null es opcional ya que la imagen no es obligatoria
    @SerializedName("tipo") val type: String,
    @SerializedName("foro") val foro: String? = null,
    // Elementos específicos de rodada
    @SerializedName("nivel") val nivel: String? = null,
    @SerializedName("id") val idRouteBase: String? = null,
    @SerializedName("distancia") val distancia: String? = null,
    @SerializedName("codigo") val codigoAsistencia: String? = null
)
data class DefaultActivityBase(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val actividades: List<ActivityBase>,
    @SerializedName("ruta") val route: Route? = null,
    @SerializedName("ubicacion") val liveLocation: List<Location>? = null
)


data class ActivitiesBase(
    @SerializedName("actividadesInscritas") val activities: List<DefaultActivityBase>
    //   @SerializedName("permisos") val permisos: List<String>
)
