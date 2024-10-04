package com.kotlin.sacalabici.data.models.activities
import com.google.gson.annotations.SerializedName

data class ActivityBase(
    @SerializedName("titulo") val title: String,
    @SerializedName("fecha") val date: String,
    @SerializedName("hora") val time: String,
    @SerializedName("personasInscritas") val peopleEnrolled: Int,
    @SerializedName("ubicacion") val location: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("duracion") val duration: String,
    @SerializedName("imagen") val imageURL: String? = null,  // El valor null es opcional ya que la imagen no es obligatoria
    @SerializedName("tipo") val type: String
)