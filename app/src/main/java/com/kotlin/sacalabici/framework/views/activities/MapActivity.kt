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

        // Definir puntos de inicio, descanso y final
        val startPoint = Point.fromLngLat(ruta.coordenadas[0].longitud, ruta.coordenadas[0].latitud)
        val stopoverPoint = Point.fromLngLat(ruta.coordenadas[descansoIndex].longitud, ruta.coordenadas[descansoIndex].latitud)
        val endPoint = Point.fromLngLat(ruta.coordenadas[finalIndex].longitud, ruta.coordenadas[finalIndex].latitud)

        // Llamar a la función que dibuja la ruta
        drawRoute(mapView, startPoint, stopoverPoint, endPoint)
    }

    private fun drawRoute(map: MapView, startPoint: Point, stopoverPoint: Point, endPoint: Point) {
        val points = listOf(startPoint, stopoverPoint, endPoint)

        // Log de los puntos de inicio, parada y fin
        Log.d("MapActivity", "Start Point: ${startPoint.latitude()}, ${startPoint.longitude()}")
        Log.d("MapActivity", "Stopover Point: ${stopoverPoint.latitude()}, ${stopoverPoint.longitude()}")
        Log.d("MapActivity", "End Point: ${endPoint.latitude()}, ${endPoint.longitude()}")

        // URL para la API de direcciones de Mapbox
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points[0].longitude()},${points[0].latitude()};${points[1].longitude()},${points[1].latitude()};${points[2].longitude()},${points[2].latitude()}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        // Log de la URL que se genera
        Log.d("MapActivity", "Directions API URL: $url")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Log de la respuesta cruda de la API
                Log.d("MapActivity", "API Response: $response")

                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")

                // Log del número de rutas obtenidas
                Log.d("MapActivity", "Number of routes: ${routes.length()}")

                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)

                    // Log de los detalles de la primera ruta
                    Log.d("MapActivity", "First Route: $route")

                    val geometry = route.getString("geometry") // Obtener polyline6

                    // Log del polyline6 antes de decodificar
                    Log.d("MapActivity", "Polyline6: $geometry")

                    val decodedPoints = decodePolyline(geometry) // Decodificar polyline6 a coordenadas

                    // Log del número de puntos decodificados
                    Log.d("MapActivity", "Decoded Points: ${decodedPoints.size}")

                    // Log de los puntos decodificados
                    decodedPoints.forEachIndexed { index, point ->
                        Log.d("MapActivity", "Point $index: ${point.latitude()}, ${point.longitude()}")
                    }

                    // Dividir la ruta en dos tramos (antes y después del punto de descanso)
                    // Dividir la ruta en dos tramos (antes y después del punto de descanso)
                    val tramo1 = decodedPoints.takeWhile {
                        it.latitude() <= stopoverPoint.latitude() && it.longitude() <= stopoverPoint.longitude()
                    }
                    val tramo2 = decodedPoints.dropWhile {
                        it.latitude() <= stopoverPoint.latitude() && it.longitude() <= stopoverPoint.longitude()
                    }


                    // Log de los puntos en cada tramo
                    Log.d("MapActivity", "Tramo 1 Points: ${tramo1.size}")
                    tramo1.forEachIndexed { index, point ->
                        Log.d("MapActivity", "Tramo 1 - Point $index: ${point.latitude()}, ${point.longitude()}")
                    }

                    Log.d("MapActivity", "Tramo 2 Points: ${tramo2.size}")
                    tramo2.forEachIndexed { index, point ->
                        Log.d("MapActivity", "Tramo 2 - Point $index: ${point.latitude()}, ${point.longitude()}")
                    }

                    withContext(Dispatchers.Main) {
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

            // Dibujar tramo 1 en rojo
            val sourceIdTramo1 = "route-source-tramo1-${System.currentTimeMillis()}"
            val layerIdTramo1 = "route-layer-tramo1-${System.currentTimeMillis()}"

            val sourceTramo1 = GeoJsonSource.Builder(sourceIdTramo1)
                .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(tramo1)))))
                .build()
            style.addSource(sourceTramo1)
            routeSources.add(sourceIdTramo1) // Agregar a la lista de fuentes

            val lineLayerTramo1 = LineLayer(layerIdTramo1, sourceIdTramo1).apply {
                lineColor("#FF0000") // Rojo
                lineWidth(3.0)
            }
            style.addLayer(lineLayerTramo1)
            routeLayers.add(layerIdTramo1) // Agregar a la lista de capas

            val sourceIdTramo2 = "route-source-tramo2-${System.currentTimeMillis()}"
            val layerIdTramo2 = "route-layer-tramo2-${System.currentTimeMillis()}"

            val sourceTramo2 = GeoJsonSource.Builder(sourceIdTramo2)
                .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(tramo2)))))
                .build()
            style.addSource(sourceTramo2)
            routeSources.add(sourceIdTramo2) // Agregar a la lista de fuentes

            val lineLayerTramo2 = LineLayer(layerIdTramo2, sourceIdTramo2).apply {
                lineColor("#228B22") // Rojo
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


    private fun addPinsForRouteType(style: Style, ruta: RutasBase) {
        // Define los iconos para diferentes tipos
        val iconImages = mapOf(
            "inicio" to R.drawable.start_icon,
            "descanso" to R.drawable.stopover_icon,
            "final" to R.drawable.end_icon
        )

        // Añade los iconos al estilo
        iconImages.forEach { (type, resId) ->
            // Añade la imagen al estilo, sin verificar si ya existe
            style.addImage(type, BitmapFactory.decodeResource(resources, resId))
        }

        // Crea una lista de características con los puntos y tipos
        val features = ruta.coordenadas.map { coordenada ->
            val properties = JsonObject().apply {
                addProperty("tipo", coordenada.tipo)
            }
            Feature.fromGeometry(
                Point.fromLngLat(coordenada.longitud, coordenada.latitud),
                properties
            )
        }

        val featureCollection = FeatureCollection.fromFeatures(features)

        // Genera un ID único para la nueva fuente y capa de pines
        val uniqueId = System.currentTimeMillis()
        val symbolSourceId = "symbol-source-$uniqueId"
        val symbolLayerId = "symbol-layer-$uniqueId"

        // Guardar los IDs en las listas
        pinSources.add(symbolSourceId)
        pinLayers.add(symbolLayerId)

        mapView.getMapboxMap().getStyle { style ->
            // Eliminar la fuente de símbolos existente si la hay
            val existingSymbolSource = style.getSourceAs<GeoJsonSource>(symbolSourceId)
            if (existingSymbolSource != null) {
                style.removeStyleSource(symbolSourceId)
            }

            // Crear y añadir la nueva fuente con la colección de características
            val symbolSource = GeoJsonSource.Builder(symbolSourceId)
                .featureCollection(featureCollection)
                .build()
            style.addSource(symbolSource)

            // Eliminar la capa de símbolos existente si la hay
            val existingSymbolLayer = style.getLayer(symbolLayerId)
            if (existingSymbolLayer != null) {
                style.removeStyleLayer(symbolLayerId)
            }

            // Añadir la nueva capa de símbolos
            val symbolLayer = SymbolLayer(symbolLayerId, symbolSourceId).apply {
                iconImage("{tipo}")
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconSize(0.07)
                iconAnchor(IconAnchor.BOTTOM)
            }

            style.addLayer(symbolLayer)
        }
    }
}
