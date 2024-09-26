package com.kotlin.sacalabici.framework.viewmodel

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.routes.RouteApiClient
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel : ViewModel() {

    val rutasListLiveData = MutableLiveData<List<RutasBase>?>()
    val routeSegmentsLiveData = MutableLiveData<Pair<List<Point>, List<Point>>>()
    val toastMessageLiveData = MutableLiveData<String>()
    var lastSelectedRuta: RutasBase? = null

    fun getRutasList() {
        viewModelScope.launch {
            try {
                val firebaseAuth = FirebaseAuth.getInstance()

                // Crear una instancia de FirebaseTokenManager con FirebaseAuth
                val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
                // Obtener el token de manera síncrona
                val routeApiClient = RouteApiClient(firebaseTokenManager)
                // Llama a getRutasList del RouteApiClient
                val rutasList = routeApiClient.getRutasList()
                rutasListLiveData.postValue(rutasList)
            } catch (e: Exception) {
                toastMessageLiveData.postValue("Error al obtener la lista de rutas: ${e.message}")
                rutasListLiveData.postValue(null)
            }
        }
    }

    fun drawRoute(coordenadas: List<CoordenadasBase>) {
        val points = coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points.joinToString(";") { "${it.longitude()},${it.latitude()}" }}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")

                if (routes.length() > 0) {
                    val geometry = routes.getJSONObject(0).getString("geometry")
                    val decodedPoints = decodePolyline(geometry)

                    val descansoIndex = points.indexOfFirst { calculateDistance(it, points[1]) < 50.0 }
                    if (descansoIndex == -1) {
                        toastMessageLiveData.postValue("El punto de descanso no se encontró en los puntos decodificados")
                        return@launch
                    }

                    val tramo1 = decodedPoints.take(descansoIndex + 1)
                    val tramo2 = decodedPoints.drop(descansoIndex)

                    withContext(Dispatchers.Main) {
                        routeSegmentsLiveData.postValue(Pair(tramo1, tramo2))
                    }
                } else {
                    toastMessageLiveData.postValue("No se encontraron rutas en la respuesta de la API")
                }
            } catch (e: Exception) {
                toastMessageLiveData.postValue("Error al obtener la ruta: ${e.message}")
            }
        }
    }

    // Función para calcular la distancia entre dos puntos en metros
    private fun calculateDistance(p1: Point, p2: Point): Double {
        val earthRadius = 6371000.0 // Radio de la Tierra en metros
        val dLat = Math.toRadians(p2.latitude() - p1.latitude())
        val dLng = Math.toRadians(p2.longitude() - p1.longitude())

        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(p1.latitude())) * cos(Math.toRadians(p2.latitude())) * sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c // Distancia en metros
    }

    // Función para decodificar la polyline
    private fun decodePolyline(encodedPolyline: String): List<Point> {
        return PolylineUtils.decode(encodedPolyline, 6).map { Point.fromLngLat(it.longitude(), it.latitude()) }
    }
}
