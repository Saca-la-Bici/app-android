package com.kotlin.sacalabici.framework.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.views.fragments.RutasFragment
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.services.RutasService
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
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
import java.net.HttpURLConnection
import java.net.URL

class MapActivity : BaseActivity(), RutasFragment.OnRutaSelectedListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var rutasFragmentVisible = false
    private var lastSelectedRuta: RutasBase? = null

    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
        initializeMap()

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
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val queretaroCoordinates = Point.fromLngLat(-100.3899, 20.5888)

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

        // Eliminar todas las rutas anteriores
        mapView.getMapboxMap().getStyle { style ->
            routeSources.forEach { sourceId -> style.getSourceAs<GeoJsonSource>(sourceId)?.let { style.removeStyleSource(sourceId) } }
            routeSources.clear()

            routeLayers.forEach { layerId -> style.getLayer(layerId)?.let { style.removeStyleLayer(layerId) } }
            routeLayers.clear()

            pinSources.forEach { sourceId -> style.getSourceAs<GeoJsonSource>(sourceId)?.let { style.removeStyleSource(sourceId) } }
            pinSources.clear()

            pinLayers.forEach { layerId -> style.getLayer(layerId)?.let { style.removeStyleLayer(layerId) } }
            pinLayers.clear()
        }

        // Zoom en la primera coordenada de la ruta
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )

        // Usar la URL de la API de Mapbox para obtener las rutas
        val coordinates = ruta.coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val points = coordinates.take(3) // Toma los primeros 3 puntos para la solicitud

        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points[0].longitude()},${points[0].latitude()};${points[1].longitude()},${points[1].latitude()};${points[2].longitude()},${points[2].latitude()}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        lifecycleScope.launch {
            val response = fetchRouteFromApi(url)
            response?.let {
                // Procesar la respuesta y extraer las coordenadas para dibujar la ruta
                val routeCoordinates = extractCoordinatesFromResponse(it)
                drawRoute(routeCoordinates)
            }
        }

        // Añadir los pines para el tipo de ruta
        addPinsForRouteType(mapView.getMapboxMap().getStyle()!!, ruta)
    }

    private suspend fun fetchRouteFromApi(url: URL): String? {
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            return@withContext if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                Log.e("MapActivity", "Error en la conexión: ${connection.responseCode}")
                null
            }
        }
    }

    private fun extractCoordinatesFromResponse(response: String): List<Point> {
        // Aquí parsearías el JSON para extraer las coordenadas
        // Este es un ejemplo simple; adapta esto a tu formato real de respuesta
        val jsonObject = JsonParser.parseString(response).asJsonObject
        val routes = jsonObject.getAsJsonArray("routes")
        val coordinates = mutableListOf<Point>()

        if (routes.size() > 0) {
            val geometry = routes[0].asJsonObject.getAsJsonObject("geometry")
            val coordinatesArray = geometry.getAsJsonArray("coordinates")

            for (i in 0 until coordinatesArray.size()) {
                val pointArray = coordinatesArray[i].asJsonArray
                val longitude = pointArray[0].asDouble
                val latitude = pointArray[1].asDouble
                coordinates.add(Point.fromLngLat(longitude, latitude))
            }
        }

        return coordinates
    }

    private fun drawRoute(coordinates: List<Point>) {
        val uniqueId = System.currentTimeMillis()
        val sourceId = "route-source-$uniqueId"
        val layerId = "route-layer-$uniqueId"

        mapView.getMapboxMap().getStyle { style ->
            val source = GeoJsonSource.Builder(sourceId)
                .featureCollection(FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(LineString.fromLngLats(coordinates)))))
                .build()
            style.addSource(source)

            val lineLayer = LineLayer(layerId, sourceId).apply {
                lineColor("#FF0000") // Rojo para la ruta
                lineWidth(3.0)
            }
            style.addLayer(lineLayer)

            // Guardar los IDs
            routeSources.add(sourceId)
            routeLayers.add(layerId)
        }
    }

    private fun addPinsForRouteType(style: Style, ruta: RutasBase) {
        val iconImages = mapOf(
            "inicio" to R.drawable.start_icon,
            "descanso" to R.drawable.stopover_icon,
            "final" to R.drawable.end_icon
        )

        iconImages.forEach { (type, resId) ->
            style.addImage(type, BitmapFactory.decodeResource(resources, resId))
        }

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

        val uniqueId = System.currentTimeMillis()
        val symbolSourceId = "symbol-source-$uniqueId"
        val symbolLayerId = "symbol-layer-$uniqueId"

        pinSources.add(symbolSourceId)
        pinLayers.add(symbolLayerId)

        mapView.getMapboxMap().getStyle { style ->
            val existingSymbolSource = style.getSourceAs<GeoJsonSource>(symbolSourceId)
            if (existingSymbolSource != null) {
                style.removeStyleSource(symbolSourceId)
            }

            val symbolSource = GeoJsonSource.Builder(symbolSourceId)
                .featureCollection(featureCollection)
                .build()
            style.addSource(symbolSource)

            val symbolLayer = SymbolLayer(symbolLayerId, symbolSourceId).apply {
                iconImage("{tipo}")
                iconAllowOverlap(true)
                iconSize(1.5)
            }
            style.addLayer(symbolLayer)
        }
    }
}