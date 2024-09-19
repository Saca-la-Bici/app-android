package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.data.network.SessionAPIClient

class SessionRepository(private val idToken: String?) {

    private val apiSession = SessionAPIClient(idToken)

    suspend fun registerUser(user: User): User? = apiSession.registerUser(user)
}