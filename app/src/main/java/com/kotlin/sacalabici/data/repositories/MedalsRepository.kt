package com.kotlin.sacalabici.data.repositories

import com.kotlin.sacalabici.data.models.medals.Medal
import com.kotlin.sacalabici.data.network.medals.MedalsAPIClient

class MedalsRepository {
    private val apiMedals = MedalsAPIClient()

    // Obtener la lista de medallas a través del cliente API
    suspend fun consultarMedallasList(): List<Medal>? {
        return apiMedals.consultarMedallasList()
    }
}
