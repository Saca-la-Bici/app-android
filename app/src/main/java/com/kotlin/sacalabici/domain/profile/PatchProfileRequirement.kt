package com.kotlin.sacalabici.domain.profile

import android.content.Context
import com.kotlin.sacalabici.data.repositories.ProfileRepository
import com.kotlin.sacalabici.data.models.profile.Profile

class PatchProfileRequirement {
    private val repository = ProfileRepository()

    suspend operator fun invoke(profile: Profile, context: Context): Profile? =
        repository.patchProfile(profile, context) // Llama la función en el repositorio apra publicar los cambios.
}
