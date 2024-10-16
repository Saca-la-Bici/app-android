package com.kotlin.sacalabici.domain.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.DeleteResponse
import com.kotlin.sacalabici.data.repositories.FAQRepository

class DeleteFaqRequirement {
    private val repository = FAQRepository()
    suspend operator fun invoke(IdPregunta: Int): DeleteResponse {
        return repository.deleteFAQ(IdPregunta)
    }
}