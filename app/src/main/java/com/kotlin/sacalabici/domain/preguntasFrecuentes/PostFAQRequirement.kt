package com.kotlin.sacalabici.domain.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.repositories.FAQRepository

class PostFAQRequirement {
    private val repository = FAQRepository()

    suspend operator fun invoke(faq: FAQBase): FAQBase? = repository.postFAQ(faq)
}
