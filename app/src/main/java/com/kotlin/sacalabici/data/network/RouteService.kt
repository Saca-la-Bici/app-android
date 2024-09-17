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

    suspend fun sendRoute(titulo: String, distancia: String, tiempo: String, nivel: String, points: List<Point>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:7070/mapa/registrarRuta")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val coordinatesArray = JSONArray().apply {
                    points.forEach { point ->
                        put(JSONObject().apply {
                            put("latitud", point.latitude())
                            put("longitud", point.longitude())
                            put("tipo", "punto")
                        })
                    }
                }

                val jsonInputString = JSONObject().apply {
                    put("titulo", titulo)
                    put("distancia", distancia)
                    put("tiempo", tiempo)
                    put("nivel", nivel)
                    put("coordenadas", coordinatesArray)
                }.toString()

                connection.outputStream.use {
                    it.write(jsonInputString.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                connection.disconnect()

                responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                Log.e("RouteService", "Error al enviar la ruta: ${e.message}")
                false
            }
        }
    }
}
