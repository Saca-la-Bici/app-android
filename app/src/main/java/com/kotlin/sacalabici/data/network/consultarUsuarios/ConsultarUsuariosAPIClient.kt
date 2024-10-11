package com.kotlin.sacalabici.data.network.consultarUsuarios

import android.util.Log
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase

class ConsultarUsuariosAPIClient(private val idToken: String?) {
    private lateinit var api: ConsultarUsuariosAPIService

    suspend fun getUsuarios(page: Int, limit: Int, rol: String): List<ConsultarUsuariosBase>? {
        api = ConsultarUsuariosModuleDI(idToken) // Asegúrate de obtener la instancia correctamente
        return try {
            val response = api.getUsuarios(page, limit, rol) // Asegúrate de pasar el firebaseUID
            response.usuarios // Devuelve la lista de usuarios
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener usuarios: ${e.message}")
            null
        }
    }

    suspend fun searchUser(username: String, roles: String? = null): List<ConsultarUsuariosBase>? {
        api = ConsultarUsuariosModuleDI(idToken) // Asegúrate de obtener la instancia correctamente
        return try {
            val response = api.searchUser(username, roles ?: "") // Asegúrate de pasar el firebaseUID
            response.usuarios // Devuelve la lista de usuarios
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener usuarios: ${e.message}")
            null
        }
    }
}