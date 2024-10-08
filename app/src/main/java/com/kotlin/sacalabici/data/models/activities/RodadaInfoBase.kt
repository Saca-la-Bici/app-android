package com.kotlin.sacalabici.data.models.activities

data class RodadaInfoBase(
    val idRodada: String?,
    val idRuta: String?,
    val message: String? = null,
    val error: Boolean = false
)