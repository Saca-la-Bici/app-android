package com.kotlin.sacalabici.data.network.model
import com.google.gson.annotations.SerializedName
import java.util.Date

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

data class ActivityBase(
    @SerializedName("_id") val id: String,
    @SerializedName("informacion") val activities: List<Activity>
)