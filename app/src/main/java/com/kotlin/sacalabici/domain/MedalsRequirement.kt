package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.models.medals.Medal
import com.kotlin.sacalabici.data.repositories.MedalsRepository

class MedalsRequirement {
    private val repository = MedalsRepository()

    // Llamar al repositorio para obtener la lista de medallas
    suspend operator fun invoke(): List<Medal>? {
        return repository.consultarMedallasList()
    }
}
