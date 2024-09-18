package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.models.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository

class ConsultarPreguntasFrecuentesRequirement {
    private val repository = PreguntasFrecuentesRepository()

    suspend operator fun invoke(): List<PreguntaFrecuente>? {
        return repository.consultarPreguntasFrecuentesList()
    }
}