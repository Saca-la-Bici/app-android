package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosAPIClient

class ConsultarUsuariosRepository(private val idToken: String?) {

    private val apiConsultarUsuarios = ConsultarUsuariosAPIClient(idToken)

    suspend fun getUsuarios(page: Int, limit: Int, roles: String): List<ConsultarUsuariosBase>? = apiConsultarUsuarios.getUsuarios(page, limit, roles)

    suspend fun searchUser(username: String): List<ConsultarUsuariosBase>? = apiConsultarUsuarios.searchUser(username)

}
