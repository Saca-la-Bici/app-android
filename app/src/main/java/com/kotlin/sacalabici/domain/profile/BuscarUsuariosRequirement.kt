package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository

class BuscarUsuariosRequirement(private val idToken: String?) {
    private val repository = ConsultarUsuariosRepository(idToken)

    suspend operator fun invoke(username: String, roles: String? = null): List<ConsultarUsuariosBase>? = repository.searchUser(username, roles)
}