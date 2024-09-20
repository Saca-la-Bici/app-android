package com.kotlin.sacalabici.data.repositories

import android.util.Log
import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosAPIService
import com.kotlin.sacalabici.data.network.consultarUsuarios.ConsultarUsuariosModuleDI

class ConsultarUsuariosRepository {
    private lateinit var api: ConsultarUsuariosAPIService

    suspend fun getUsuarios(limit: Int): List<ConsultarUsuariosBase>? {
        api = ConsultarUsuariosModuleDI() // Asegúrate de obtener la instancia correctamente
        return try {
            api.getUsuarios(limit)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener usuarios: ${e.message}")
            null
        }
    }
}
