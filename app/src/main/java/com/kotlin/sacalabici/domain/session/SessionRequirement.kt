package com.kotlin.sacalabici.domain.session

import com.kotlin.sacalabici.data.models.session.User
import com.kotlin.sacalabici.data.repositories.session.SessionRepository

class SessionRequirement(private val idToken: String?) {

    private val repository = SessionRepository(idToken)

    suspend operator fun invoke(user: User): User? = repository.registerUser(user)
}