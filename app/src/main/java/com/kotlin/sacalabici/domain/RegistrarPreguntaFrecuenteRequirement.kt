package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.models.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository

class RegistrarPreguntaFrecuenteRequirement {
    private val repository = PreguntasFrecuentesRepository()

    suspend operator fun invoke(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? {
        return repository.registrarPreguntaFrecuente(preguntaFrecuente)
    }
}