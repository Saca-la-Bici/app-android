package com.kotlin.sacalabici.framework.services

import android.util.Log
import com.google.gson.Gson
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.data.models.RutasObject
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
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
                // Parsear el JSON usando Gson
                val rutasObject = Gson().fromJson(responseMessage, RutasObject::class.java)
                rutasObject.rutas
            } else {
                Log.e("getRutasList", "Error en la solicitud: $responseCode - $responseMessage")
                null
            }
        } catch (e: Exception) {
            Log.e("getRutasList", "Excepción en la solicitud: ${e.message}")
            null
        }
    }

    suspend fun modifyRoute(
        id: String,
        titulo: String,
        distancia: String,
        tiempo: String,
        nivel: String,
        start: Point,
        stopover: Point,
        end: Point,
    ): Boolean = withContext(Dispatchers.IO) {   // Ejecuta la lógica con corrutinas
        try {
            // URL de la solicitud POST para registrar la ruta
            val url = URL("http://3.145.117.182:8080/mapa/modificarRuta/$id")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true // Indica que se enviarán datos en la solicitud

            // Se crea un arreglo JSON con los puntos de inicio, descanso y final
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

            // Crea el objeto JSON con la información de la ruta a enviar
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

            // Se obtiene la respuesta del servidor
            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            // Se cierra la conexión después de obtener la respuesta
            connection.disconnect()

            // Si la respuesta es OK, regresa true, si no, regresa false
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

    suspend fun sendRoute(
        titulo: String,
        distancia: String,
        tiempo: String,
        nivel: String,
        start: Point,
        stopover: Point,
        end: Point
    ): Boolean = withContext(Dispatchers.IO) {   // Ejecuta la lógica con corrutinas
        try {
            // URL de la solicitud POST para registrar la ruta
            val url = URL("http://3.145.117.182:8080/mapa/registrarRuta")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true // Indica que se enviarán datos en la solicitud

            // Se crea un arreglo JSON con los puntos de inicio, descanso y final
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

            // Crea el objeto JSON con la información de la ruta a enviar
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

            // Se obtiene la respuesta del servidor
            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            // Se cierra la conexión después de obtener la respuesta
            connection.disconnect()

            // Si la respuesta es OK, regresa true, si no, regresa false
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
