package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.network.model.ProfileBase
import com.kotlin.sacalabici.data.network.model.profile.Profile
import com.kotlin.sacalabici.data.repositories.ProfileRepository

class GetProfileRequirement {
    private val repository = ProfileRepository()
    suspend operator fun invoke(
        userId: String
    ): ProfileBase? = repository.getUsuario(userId)
}
