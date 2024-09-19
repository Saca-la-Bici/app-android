package com.kotlin.sacalabici.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object RutasService {
    // Función que obtiene las rutas del servidor
    // Recibe los datos de la ruta y los puntos de inicio, descanso y fin
    suspend fun getRutasList(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http:3.145.117.182:8080/mapa/consultarRutas")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Se obtiene la respuesta del servidor
            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            // Se cierra la conexión después de recibir la respuesta
            connection.disconnect()

            // Si la respuesta es OK, regresa true
            // Si no, regresa false
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("getRutasList", "Respuesta del servidor: $responseMessage")
                true
            } else {
                Log.e("getRutasList", "Error en la solicitud: $responseCode - $responseMessage")
                false
            }
        } catch (e: Exception) {
            Log.e("getRutasList", "Excepción en la solicitud: ${e.message}")
            false
        }
    }
}