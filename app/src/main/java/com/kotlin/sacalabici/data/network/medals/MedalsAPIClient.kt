package com.kotlin.sacalabici.data.network.medals

import com.kotlin.sacalabici.data.models.medals.Medal

class MedalsAPIClient {
    private val api: MedalsAPIService = MedalsModuleDI()

    // Consulta la lista de medallas y maneja cualquier excepción
    suspend fun consultarMedallasList(): List<Medal>? {
        return try {
            api.consultarMedallasList() // Llama al método consultarMedallas del servicio
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
