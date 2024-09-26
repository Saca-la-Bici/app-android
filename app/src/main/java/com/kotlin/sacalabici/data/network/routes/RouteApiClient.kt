package com.kotlin.sacalabici.data.network.routes

import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.mapbox.geojson.Point

class RouteApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    private lateinit var api: RouteApiService

    // Obtener la lista de rutas
    suspend fun getRutasList(): List<RutasBase> {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        return if (token != null) {
            api = RouteNetworkModuleDI(token)
            try {
                api.getRutasList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Modificar una ruta existente
    suspend fun modifyRoute(
        id: String,
        titulo: String,
        distancia: String,
        tiempo: String,
        nivel: String,
        start: Point,
        stopover: Point,
        end: Point,
        reference1: Point,
        reference2: Point
    ): Pair<Boolean, String?> { // Cambia el retorno a un Pair
        val token = firebaseTokenManager.getTokenSynchronously()
        api = RouteNetworkModuleDI(token)

        // Crear lista de coordenadas basadas en Points
        val coordenadas = arrayListOf(
            CoordenadasBase(start.latitude(), start.longitude(), "start"),
            CoordenadasBase(reference1.latitude(), reference1.longitude(), "reference1"),
            CoordenadasBase(stopover.latitude(), stopover.longitude(), "stopover"),
            CoordenadasBase(reference2.latitude(), reference2.longitude(), "reference2"),
            CoordenadasBase(end.latitude(), end.longitude(), "end")
        )

        return try {
            // Crear el objeto RutasBase con las coordenadas
            val rutasBase = RutasBase(
                id = id,
                titulo = titulo,
                distancia = distancia,
                tiempo = tiempo,
                nivel = nivel,
                coordenadas = coordenadas
            )

            // Hacer la solicitud para modificar la ruta
            val response = api.modifyRoute(id, rutasBase)
            if (response.isSuccessful) {
                Pair(true, null) // Retornar éxito sin error
            } else {
                Pair(false, response.errorBody()?.string()) // Retornar error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, e.message) // Retornar error
        }
    }


    // Registrar una nueva ruta
    suspend fun sendRoute(
        titulo: String,
        distancia: String,
        tiempo: String,
        nivel: String,
        start: Point,
        stopover: Point,
        end: Point,
        reference1: Point,
        reference2: Point
    ): Boolean {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = RouteNetworkModuleDI(token)

        // Crear lista de coordenadas basadas en Points
        val coordenadas = arrayListOf(
            CoordenadasBase(start.latitude(), start.longitude(), "start"),
            CoordenadasBase(reference1.latitude(), reference1.longitude(), "reference1"),
            CoordenadasBase(stopover.latitude(), stopover.longitude(), "stopover"),
            CoordenadasBase(reference2.latitude(), reference2.longitude(), "reference2"),
            CoordenadasBase(end.latitude(), end.longitude(), "end")
        )

        return try {
            // Crear el objeto RutasBase con las coordenadas
            val rutasBase = RutasBase(
                id = "",  // Enviar un id vacío o algún valor predeterminado si el backend lo genera
                titulo = titulo,
                distancia = distancia,
                tiempo = tiempo,
                nivel = nivel,
                coordenadas = coordenadas
            )

            // Hacer la solicitud para registrar la nueva ruta
            val response = api.sendRoute(rutasBase)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
