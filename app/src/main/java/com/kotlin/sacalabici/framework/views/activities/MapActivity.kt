package com.kotlin.sacalabici.framework.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.kotlin.sacalabici.framework.views.fragments.RutasFragment
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.services.RutasService
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
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MapActivity : BaseActivity(), RutasFragment.OnRutaSelectedListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var rutasFragmentVisible = false
    private var currentSourceId: String? = null
    private var currentLayerId: String? = null
    private var lastSelectedRuta: RutasBase? = null

    // Para almacenar las fuentes y capas de rutas
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    // Para almacenar las fuentes y capas de pines
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
        initializeMap()

        val btnDetails: ImageButton = findViewById(R.id.btnDetails)

        // Botón para mostrar/ocultar la lista de rutas
        btnDetails.setOnClickListener {
            toggleRutasList()
        }
    }

    private fun initializeBinding(){
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
    }

    private fun initializeMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            // Define las coordenadas para Querétaro
            val queretaroCoordinates = Point.fromLngLat(-100.3899, 20.5888)

            // Mueve la cámara a Querétaro
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(12.0)
                    .build()
            )
        }
    }

    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

        if (fragment != null && fragment is RutasFragment) {
            fragmentManager.beginTransaction().remove(fragment).commit()
            rutasFragmentVisible = false
        } else {
            lifecycleScope.launch {
                val rutasList = RutasService.getRutasList()
                if (rutasList != null) {
                    val rutasFragment = RutasFragment.newInstance(rutasList, lastSelectedRuta)
                    fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, rutasFragment)
                        .addToBackStack(null)
                        .commit()
                    rutasFragmentVisible = true
                } else {
                    Log.e("MapActivity", "Error al obtener la lista de rutas")
                }
            }
        }
    }

    override fun onRutaSelected(ruta: RutasBase) {
        lastSelectedRuta = ruta

        // Eliminar rutas previas del mapa
        clearPreviousRoutes()

        // Zoom en la primera coordenada de la ruta
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )

        // Verificar si hay puntos de descanso y final
        val descansoIndex = ruta.coordenadas.indexOfFirst { it.tipo == "descanso" }
        val finalIndex = ruta.coordenadas.indexOfFirst { it.tipo == "final" }

        if (descansoIndex == -1 || finalIndex == -1) {
            Log.e("MapActivity", "No se encontraron puntos de descanso o final en la ruta")
            return
        }

        // Llamar a la función que dibuja la ruta con todas las coordenadas
        drawRoute(mapView, ruta.coordenadas)

        // Agregar los pines a los puntos relevantes
        val startPoint = Point.fromLngLat(ruta.coordenadas[0].longitud, ruta.coordenadas[0].latitud)
        val stopoverPoint = Point.fromLngLat(ruta.coordenadas[descansoIndex].longitud, ruta.coordenadas[descansoIndex].latitud)
        val endPoint = Point.fromLngLat(ruta.coordenadas[finalIndex].longitud, ruta.coordenadas[finalIndex].latitud)
        addPins(mapView, startPoint, stopoverPoint, endPoint)
    }


    fun coordenadasToPoint(coordenada: CoordenadasBase): Point {
        return Point.fromLngLat(coordenada.longitud, coordenada.latitud)
    }

    private fun drawRoute(map: MapView, coordenadas: List<CoordenadasBase>) {
        // Asegúrate de que haya al menos 3 puntos: inicio, descanso, final
        if (coordenadas.size < 3) {
            Log.e("MapActivity", "Faltan puntos en las coordenadas")
            return
        }

        // Convertir los objetos CoordenadasBase a Point
        val points = coordenadas.map { coordenadasToPoint(it) }

        // Generar la URL para obtener la ruta desde la API de Mapbox
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points[0].longitude()},${points[0].latitude()};${points[1].longitude()},${points[1].latitude()};${points[2].longitude()},${points[2].latitude()}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")

                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val geometry = route.getString("geometry")

                    // Decodificar los puntos de la geometría (polyline)
                    val decodedPoints = decodePolyline(geometry)

                    // Separar los tramos usando las coordenadas del punto de descanso
                    val puntoDescanso = points[1]

                    val tramo1 = decodedPoints.takeWhile {
                        it.latitude() != puntoDescanso.latitude() && it.longitude() != puntoDescanso.longitude()
                    }
                    val tramo2 = decodedPoints.dropWhile {
                        it.latitude() != puntoDescanso.latitude() && it.longitude() != puntoDescanso.longitude()
                    }

                    withContext(Dispatchers.Main) {
                        // Dibujar los dos segmentos con colores diferentes
                        drawRouteSegments(map, tramo1, tramo2)
                    }
                }
            } catch (e: Exception) {
                Log.e("MapActivity", "Error al obtener la ruta: ${e.message}")
            }
        }
    }



    private fun drawRouteSegments(map: MapView, tramo1: List<Point>, tramo2: List<Point>) {
        map.getMapboxMap().getStyle { style ->

            // Dibujar tramo 1 en rojo (antes del punto de descanso)
            val sourceIdTramo1 = "route-source-tramo1-${System.currentTimeMillis()}"
            val layerIdTramo1 = "route-layer-tramo1-${System.currentTimeMillis()}"

            val sourceTramo1 = GeoJsonSource.Builder(sourceIdTramo1)
                .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(tramo1)))))
                .build()
            style.addSource(sourceTramo1)
            routeSources.add(sourceIdTramo1) // Agregar a la lista de fuentes

            val lineLayerTramo1 = LineLayer(layerIdTramo1, sourceIdTramo1).apply {
                lineColor("#FF0000") // Rojo para el primer tramo
                lineWidth(3.0)
            }
            style.addLayer(lineLayerTramo1)
            routeLayers.add(layerIdTramo1) // Agregar a la lista de capas

            // Dibujar tramo 2 en verde (después del punto de descanso)
            val sourceIdTramo2 = "route-source-tramo2-${System.currentTimeMillis()}"
            val layerIdTramo2 = "route-layer-tramo2-${System.currentTimeMillis()}"

            val sourceTramo2 = GeoJsonSource.Builder(sourceIdTramo2)
                .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(tramo2)))))
                .build()
            style.addSource(sourceTramo2)
            routeSources.add(sourceIdTramo2) // Agregar a la lista de fuentes

            val lineLayerTramo2 = LineLayer(layerIdTramo2, sourceIdTramo2).apply {
                lineColor("#228B22") // Verde para el segundo tramo
                lineWidth(3.0)
            }
            style.addLayer(lineLayerTramo2)
            routeLayers.add(layerIdTramo2) // Agregar a la lista de capas
        }
    }


    private fun decodePolyline(encodedPolyline: String): List<Point> {
        return PolylineUtils.decode(encodedPolyline, 6).map { Point.fromLngLat(it.longitude(), it.latitude()) }
    }

    private fun clearPreviousRoutes() {
        mapView.getMapboxMap().getStyle { style ->
            // Eliminar las fuentes de rutas anteriores
            routeSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            routeSources.clear() // Limpiamos la lista de fuentes

            // Eliminar las capas de rutas anteriores
            routeLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            routeLayers.clear() // Limpiamos la lista de capas

            // Eliminar las fuentes de pines anteriores
            pinSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            pinSources.clear() // Limpiamos la lista de fuentes de pines

            // Eliminar las capas de pines anteriores
            pinLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            pinLayers.clear() // Limpiamos la lista de capas de pines
        }
    }

    private fun addPins(map: MapView, startPoint: Point, stopoverPoint: Point, endPoint: Point) {
        map.getMapboxMap().getStyle { style ->

            // Definir íconos personalizados para los pines (inicio, descanso, final)
            val startPinIcon = BitmapFactory.decodeResource(resources, R.drawable.start_icon)
            val stopoverPinIcon = BitmapFactory.decodeResource(resources, R.drawable.stopover_icon)
            val endPinIcon = BitmapFactory.decodeResource(resources, R.drawable.end_icon)

            // Agregar los íconos personalizados al estilo de Mapbox
            style.addImage("start-pin-icon", startPinIcon)
            style.addImage("stopover-pin-icon", stopoverPinIcon)
            style.addImage("end-pin-icon", endPinIcon)

            // Crear y agregar pines de inicio, descanso y final
            val pinPoints = listOf(
                Pair(startPoint, "start-pin-icon"),
                Pair(stopoverPoint, "stopover-pin-icon"),
                Pair(endPoint, "end-pin-icon")
            )

            pinPoints.forEachIndexed { index, (point, iconName) ->
                val sourceId = "pin-source-$index-${System.currentTimeMillis()}"
                val layerId = "pin-layer-$index-${System.currentTimeMillis()}"

                val source = GeoJsonSource.Builder(sourceId)
                    .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(point))))
                    .build()
                style.addSource(source)
                pinSources.add(sourceId) // Agregar a la lista de fuentes de pines

                val symbolLayer = SymbolLayer(layerId, sourceId)
                    .iconImage(iconName) // Usar el nombre del ícono como cadena de texto
                    .iconAllowOverlap(true)
                    .iconIgnorePlacement(true)
                    .iconSize(0.07)
                    .iconAnchor(IconAnchor.BOTTOM)
                style.addLayer(symbolLayer)
                pinLayers.add(layerId) // Agregar a la lista de capas de pines
            }
        }
    }


}
