package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.data.repositories.SessionRepository

class SessionRequirement {
    private val repository = SessionRepository()

    suspend operator fun invoke(
        user: User
    ): User? = repository.registerUser(user)
}