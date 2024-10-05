package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.repositories.ProfileRepository
import com.kotlin.sacalabici.data.models.profile.Profile

class PatchProfileRequirement {
    private val repository = ProfileRepository()

    suspend operator fun invoke(profile: Profile): Profile? =
        repository.patchProfile(profile)
}
