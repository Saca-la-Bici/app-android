package com.kotlin.sacalabici.data.repositories.session

import com.kotlin.sacalabici.data.models.session.PerfilCompletoObject
import com.kotlin.sacalabici.data.models.user.User
import com.kotlin.sacalabici.data.network.session.SessionAPIClient

class SessionRepository(private val idToken: String?) {

    private val apiSession = SessionAPIClient(idToken)

    suspend fun registerUser(user: User): User? = apiSession.registerUser(user)

    suspend fun getUser(): PerfilCompletoObject? = apiSession.getUser()
}