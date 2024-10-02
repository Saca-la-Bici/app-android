package com.kotlin.sacalabici.domain.preguntasFrecuentes

import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.repositories.FAQRepository

// Caso de uso en la capa de dominio.
// Encapsula la lógica para obtener una lista de FAQ de la API
// La clase interactúa con el PokemonRepository para recuperar los datos.
class FAQListRequirement {
    private val repository = FAQRepository()

    suspend operator fun invoke(): List<FAQBase> = repository.getFAQList()
}
