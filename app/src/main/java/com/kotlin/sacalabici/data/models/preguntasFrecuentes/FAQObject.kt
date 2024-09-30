package com.kotlin.sacalabici.data.models.preguntasFrecuentes

import com.google.gson.annotations.SerializedName

// Clase de datos que representa una lista de preguntas frecuentes
data class FAQObject(
    @SerializedName("count") val count: Int,

    // Contiene la lista de objetos FAQBase
    @SerializedName("results") val results: ArrayList<FAQBase>
)
