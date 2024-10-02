package com.kotlin.sacalabici.domain.medals

import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.data.repositories.medals.MedalsRepository

class ConsultMedalsListRequirement {
    private val repository = MedalsRepository()

    suspend operator fun invoke(
    ): List<MedalBase>? = repository.getMedalsList()
}