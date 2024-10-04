package com.kotlin.sacalabici.data.repositories.session

import com.kotlin.sacalabici.data.models.session.GetUsernameObject
import com.kotlin.sacalabici.data.network.session.RegisterUserAPIClient

class RegisterUserRepository {
    private val apiSession = RegisterUserAPIClient()

    suspend fun validateUser(username: String): GetUsernameObject? = apiSession.verifyUser(username)
}