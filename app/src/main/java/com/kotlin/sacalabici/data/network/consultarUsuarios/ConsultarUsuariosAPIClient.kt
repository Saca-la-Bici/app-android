package com.kotlin.sacalabici.data.network.consultarUsuarios

import android.util.Log
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase

class ConsultarUsuariosAPIClient {
    private lateinit var api: ConsultarUsuariosAPIService

    suspend fun getUsuarios(limit: Int): List<ConsultarUsuariosBase>? {
        api = ConsultarUsuariosModuleDI() // Asegúrate de obtener la instancia correctamente
        return try {
            val response = api.getUsuarios(limit)
            response.usuarios // Devuelve la lista de usuarios
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener usuarios: ${e.message}")
            null
        }
    }

    suspend fun searchUser(username: String): List<ConsultarUsuariosBase>? {
        api = ConsultarUsuariosModuleDI() // Asegúrate de obtener la instancia correctamente
        return try {
            val response = api.searchUser(username)
            response.usuarios // Devuelve la lista de usuarios
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener usuarios: ${e.message}")
            null
        }
    }
}