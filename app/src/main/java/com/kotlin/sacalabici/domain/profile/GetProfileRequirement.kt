package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.repositories.ProfileRepository

class GetProfileRequirement {
    private val repository = ProfileRepository()
    suspend operator fun invoke(
    ): ProfileBase? = repository.getUsuario()
    }

