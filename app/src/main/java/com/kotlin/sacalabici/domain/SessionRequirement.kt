package com.kotlin.sacalabici.domain

import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.data.repositories.SessionRepository

class SessionRequirement(private val idToken: String?) {

    private val repository = SessionRepository(idToken)

    suspend operator fun invoke(user: User): User? = repository.registerUser(user)
}