package com.kotlin.sacalabici.domain.session

import com.kotlin.sacalabici.data.models.session.GetUsernameObject
import com.kotlin.sacalabici.data.repositories.session.RegisterUserRepository

class RegisterUserRequirement {
    private val repository = RegisterUserRepository()

    suspend operator fun invoke(username: String): GetUsernameObject? = repository.validateUser(username)
}