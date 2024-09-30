package com.kotlin.sacalabici.domain.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository

class ConsultFAQListRequirement {
    private val repository = PreguntasFrecuentesRepository()

    suspend operator fun invoke(): List<PreguntaFrecuente>? {
        return repository.consultarPreguntasFrecuentesList()
    }
}