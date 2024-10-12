package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.repositories.ProfileRepository

class DeleteProfileRequirement {
    private val repository = ProfileRepository()

    suspend operator fun invoke(): Boolean{
        return repository.deleteProfile()
    }
}