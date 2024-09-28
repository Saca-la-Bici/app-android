package com.kotlin.sacalabici.domain.session

import com.kotlin.sacalabici.data.models.session.PerfilCompletoObject
import com.kotlin.sacalabici.data.models.user.User
import com.kotlin.sacalabici.data.repositories.session.SessionRepository

class SessionRequirement(private val idToken: String?) {

    private val repository = SessionRepository(idToken)

    suspend operator fun invoke(user: User): User? = repository.registerUser(user)

    suspend fun getUser(): PerfilCompletoObject? = repository.getUser()
}