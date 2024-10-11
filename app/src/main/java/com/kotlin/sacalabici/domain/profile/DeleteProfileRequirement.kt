package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.data.repositories.ProfileRepository

class DeleteProfileRequirement {
    private val repository = ProfileRepository()

    suspend operator fun invoke(): Profile? =
        repository.deleteProfile()
}