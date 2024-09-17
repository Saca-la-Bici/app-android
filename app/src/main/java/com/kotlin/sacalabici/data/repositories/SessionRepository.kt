package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.User
import com.kotlin.sacalabici.data.network.SessionAPIClient

class SessionRepository {
    private val apiSession = SessionAPIClient()

    suspend fun registerUser(user: User): User? = apiSession.registerUser(user)
}