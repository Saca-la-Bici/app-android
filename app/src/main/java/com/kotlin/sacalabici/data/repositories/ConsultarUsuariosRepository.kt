package com.kotlin.sacalabici.data.repositories

import android.util.Log
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.models.user.User
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosAPIClient
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosAPIService
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosModuleDI
import com.kotlin.sacalabici.data.network.session.SessionAPIClient

class ConsultarUsuariosRepository(private val idToken: String?) {

    private val apiConsultarUsuarios = ConsultarUsuariosAPIClient(idToken)

    suspend fun getUsuarios(page: Int, limit: Int): List<ConsultarUsuariosBase>? = apiConsultarUsuarios.getUsuarios(page, limit)

    suspend fun searchUser(username: String): List<ConsultarUsuariosBase>? = apiConsultarUsuarios.searchUser(username)

}
