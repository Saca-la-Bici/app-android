package com.kotlin.sacalabici.domain.profile

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository

class ConsultarUsuariosRequirement(private val idToken: String?) {
    private val repository = ConsultarUsuariosRepository(idToken)

    suspend operator fun invoke(page: Int, limit: Int, roles: String, firebaseUID: String): List<ConsultarUsuariosBase>? = repository.getUsuarios(page, limit, roles, firebaseUID)

}