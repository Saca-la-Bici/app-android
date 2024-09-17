package com.kotlin.sacalabici.data.network

import android.util.Log
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object RouteService {

    suspend fun sendRoute(titulo: String, distancia: String, tiempo: String, nivel: String, start: Point, stopover: Point, end: Point
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:7070/mapa/registrarRuta")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val coordinatesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("latitud", start.latitude())
                    put("longitud", start.longitude())
                    put("tipo", "inicio")
                })
                put(JSONObject().apply {
                    put("latitud", stopover.latitude())
                    put("longitud", stopover.longitude())
                    put("tipo", "descanso")
                })
                put(JSONObject().apply {
                    put("latitud", end.latitude())
                    put("longitud", end.longitude())
                    put("tipo", "final")
                })
            }

            val jsonInputString = JSONObject().apply {
                put("titulo", titulo)
                put("distancia", distancia)
                put("tiempo", tiempo)
                put("nivel", nivel)
                put("coordenadas", coordinatesArray)
            }.toString()

            // Log del JSON que se va a enviar
            Log.d("sendRoute", "JSON a enviar: $jsonInputString")

            connection.outputStream.use {
                it.write(jsonInputString.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            connection.disconnect()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("sendRoute", "Respuesta del servidor: $responseMessage")
                true
            } else {
                Log.e("sendRoute", "Error en la solicitud: $responseCode - $responseMessage")
                false
            }
        } catch (e: Exception) {
            Log.e("sendRoute", "Excepción en la solicitud: ${e.message}")
            false
        }
    }
}
