package com.kotlin.sacalabici.framework.services

import android.util.Log
import com.google.gson.Gson
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.data.models.RutasObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object RutasService {

    suspend fun getRutasList(): List<RutasBase>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://3.145.117.182:8080/mapa/consultarRutas")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Obtener la respuesta del servidor
            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            connection.disconnect()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Parsear el JSON como una lista directamente
                val rutasList = Gson().fromJson(responseMessage, Array<RutasBase>::class.java).toList()
                rutasList
            } else {
                Log.e("getRutasList", "Error en la solicitud: $responseCode - $responseMessage")
                null
            }
        } catch (e: Exception) {
            Log.e("getRutasList", "Excepción en la solicitud: ${e.message}")
            null
        }
    }

}
