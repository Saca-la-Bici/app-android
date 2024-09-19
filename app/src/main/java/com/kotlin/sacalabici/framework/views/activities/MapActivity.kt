package com.kotlin.sacalabici.framework.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
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
import kotlinx.coroutines.launch

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
        // Eliminar todas las rutas anteriores
        mapView.getMapboxMap().getStyle { style ->
            // Eliminar todas las fuentes de rutas
            routeSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            routeSources.clear()

            // Eliminar todas las capas de rutas
            routeLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            routeLayers.clear()

            // Eliminar todos los pines anteriores
            pinSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            pinSources.clear()

            pinLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            pinLayers.clear()
        }

        // Genera un ID único para la nueva fuente y capa
        val uniqueId = System.currentTimeMillis() // O cualquier otro método para generar un ID único
        currentSourceId = "route-source-$uniqueId"
        currentLayerId = "route-layer-$uniqueId"

        // Guardar los IDs en las listas
        routeSources.add(currentSourceId!!)
        routeLayers.add(currentLayerId!!)

        // Zoom in en la primera coordenada de la ruta
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )

        // Crear una FeatureCollection con las coordenadas de la ruta
        val coordinates = ruta.coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val featureCollection = FeatureCollection.fromFeatures(
            arrayOf(Feature.fromGeometry(LineString.fromLngLats(coordinates)))
        )

        mapView.getMapboxMap().getStyle { style ->
            // Crear y añadir la nueva fuente con la colección de características
            val newSource = GeoJsonSource.Builder(currentSourceId!!)
                .featureCollection(featureCollection)
                .build()
            style.addSource(newSource)

            // Crear y añadir la nueva capa
            val lineLayer = LineLayer(currentLayerId!!, currentSourceId!!).apply {
                lineColor("#FF0000")
                lineWidth(3.0)
            }
            style.addLayer(lineLayer)

            // Añadir los pines para el tipo de ruta
            addPinsForRouteType(style, ruta)
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
