package com.kotlin.sacalabici.domain.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository

class RegisterFAQRequirement {
    private val repository = PreguntasFrecuentesRepository()

    suspend operator fun invoke(preguntaFrecuente: PreguntaFrecuente): PreguntaFrecuente? {
        return repository.registrarPreguntaFrecuente(preguntaFrecuente)
    }
}