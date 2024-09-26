package com.kotlin.sacalabici.framework.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.services.RutasService
import com.kotlin.sacalabici.framework.views.fragments.RutasFragment
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*

class MapActivity : AppCompatActivity(), RutasFragment.OnRutaSelectedListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var lastSelectedRuta: RutasBase? = null

    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeMap()

        val btnAgregarRuta: ImageButton = findViewById(R.id.btnAdd)
        btnAgregarRuta.setOnClickListener {
            Log.d("AgregarRuta", "Se inicia la actividad")
            val intent = Intent(this, RegistrarRutaActivity::class.java)
            startActivity(intent)
        }

        val btnDetails: ImageButton = findViewById(R.id.btnDetails)
        btnDetails.setOnClickListener {
            toggleRutasList()
        }
    }

    private fun initializeBinding() {
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
    }

    private fun initializeMap() {
        Log.d("InitializeMap", "Se inicia el mapa") // Log para indicar que se inició la inicialización del mapa
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            Log.d("InitializeMap", "Se cargó el estilo del mapa") // Log para indicar que se cargó el estilo
            val queretaroCoordinates = Point.fromLngLat(-100.4091, 20.5925)
            Log.d("InitializeMap", "Coordenadas de Querétaro: $queretaroCoordinates") // Log para mostrar coordenadas
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(15.0)
                    .build()
            )
            Log.d("InitializeMap", "Se configuró la cámara del mapa") // Log para indicar que se configuró la cámara
        }
    }


    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

        if (fragment != null && fragment is RutasFragment) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        } else {
            lifecycleScope.launch {
                try {
                    val rutasList = RutasService.getRutasList()
                    if (rutasList != null) {
                        val rutasFragment = RutasFragment.newInstance(rutasList, lastSelectedRuta)
                        fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, rutasFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        showToast("Error al obtener la lista de rutas.")
                    }
                } catch (e: Exception) {
                    showToast("Error al obtener la lista de rutas: ${e.message}")
                }
            }
        }
    }

    override fun onRutaSelected(ruta: RutasBase) {
        lastSelectedRuta = ruta
        clearPreviousRoutes()
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )

        drawRoute(mapView, ruta.coordenadas)
        addPinsToMap(ruta.coordenadas)
    }

    private fun drawRoute(map: MapView, coordenadas: List<CoordenadasBase>) {
        val points = coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points.joinToString(";") { "${it.longitude()},${it.latitude()}" }}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        lifecycleScope.launch(Dispatchers.IO) {
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
                        showToast("El punto de descanso no se encontró en los puntos decodificados")
                        return@launch
                    }

                    val tramo1 = decodedPoints.take(descansoIndex + 1)
                    val tramo2 = decodedPoints.drop(descansoIndex)

                    withContext(Dispatchers.Main) {
                        drawRouteSegments(map, tramo1, tramo2)
                    }
                } else {
                    showToast("No se encontraron rutas en la respuesta de la API")
                }
            } catch (e: Exception) {
                showToast("Error al obtener la ruta: ${e.message}")
            }
        }
    }

    private fun drawRouteSegments(map: MapView, tramo1: List<Point>, tramo2: List<Point>) {
        map.getMapboxMap().getStyle { style ->
            addRouteLayer(style, tramo1, Color.RED)
            addRouteLayer(style, tramo2, Color.GREEN)
        }
    }

    private fun addRouteLayer(style: Style, points: List<Point>, color: Int) {
        val sourceId = "route-source-${System.currentTimeMillis()}"
        val layerId = "route-layer-${System.currentTimeMillis()}"

        val source = GeoJsonSource.Builder(sourceId)
            .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(points)))))
            .build()

        style.addSource(source)
        routeSources.add(sourceId)

        val lineLayer = LineLayer(layerId, sourceId).apply {
            lineColor(color.toString())
            lineWidth(3.0)
        }
        style.addLayer(lineLayer)
        routeLayers.add(layerId)
    }

    private fun addPinsToMap(coordenadas: List<CoordenadasBase>) {
        mapView.getMapboxMap().getStyle { style ->
            coordenadas.forEachIndexed { index, coordenada ->
                val symbolLayerId = "symbol-layer-$index"
                val point = Point.fromLngLat(coordenada.longitud, coordenada.latitud)
                val symbolLayer = GeoJsonSource.Builder(symbolLayerId)
                    .feature(Feature.fromGeometry(point))
                    .build()

                style.addSource(symbolLayer)
                style.addLayer(
                    SymbolLayer(symbolLayerId, symbolLayerId).apply {
                        iconImage(getIconNameForPoint(index, coordenadas.size))
                        iconSize(1.0)
                        iconAllowOverlap(true)
                    }
                )
            }
        }
    }

    private fun getIconNameForPoint(index: Int, total: Int): String {
        return when (index) {
            0 -> "icono_inicio" // Icono de inicio de la ruta
            total - 1 -> "icono_final" // Icono de fin de la ruta
            else -> "icono_descanso" // Icono de punto de descanso
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearPreviousRoutes() {
        mapView.getMapboxMap().getStyle { style ->
            routeSources.forEach { sourceId ->
                style.removeStyleSource(sourceId)
            }
            routeLayers.forEach { layerId ->
                style.removeStyleLayer(layerId)
            }
            routeSources.clear()
            routeLayers.clear()
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
