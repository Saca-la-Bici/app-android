package com.kotlin.sacalabici.framework.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.RutasFragment
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
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.launch

class MapActivity : BaseActivity(), RutasFragment.OnRutaSelectedListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var rutasFragmentVisible = false // Variable para el estado del fragmento

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
                    .zoom(12.0) // Ajusta el zoom según sea necesario
                    .build()
            )
        }
    }

    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

        if (fragment != null && fragment is RutasFragment) {
            // Si el fragmento ya está visible, lo removemos
            fragmentManager.beginTransaction().remove(fragment).commit()
            rutasFragmentVisible = false
        } else {
            // Si el fragmento no está visible, lo agregamos
            lifecycleScope.launch {
                val rutasList = RutasService.getRutasList()
                if (rutasList != null) {
                    val rutasFragment = RutasFragment.newInstance(rutasList)
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
        // Zoom in on the first coordinate of the route
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(14.0) // Ajusta el nivel de zoom según sea necesario
                .build()
        )

        // Create a FeatureCollection with the route coordinates
        val coordinates = ruta.coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val featureCollection = FeatureCollection.fromFeatures(
            arrayOf(Feature.fromGeometry(LineString.fromLngLats(coordinates)))
        )

        mapView.getMapboxMap().getStyle { style ->
            // Check if the source already exists
            val existingSource = style.getSourceAs<GeoJsonSource>("route-source")
            if (existingSource != null) {
                // If the source exists, remove it before adding the new data
                style.removeStyleSource("route-source")
            }

            // Now create and add the new source with the updated feature collection
            val newSource = GeoJsonSource.Builder("route-source")
                .featureCollection(featureCollection)
                .build()
            style.addSource(newSource)

            // Check if the layer already exists
            val existingLayer = style.getLayer("route-layer")
            if (existingLayer == null) {
                // If the layer doesn't exist, create it
                val lineLayer = LineLayer("route-layer", "route-source").apply {
                    lineColor("#FF0000") // Set the route color
                    lineWidth(3.0)
                }
                style.addLayer(lineLayer)
            }

            // Add pins for each coordinate based on the route type
            addPinsForRouteType(style, ruta)
        }
    }

    private fun addPinsForRouteType(style: Style, ruta: RutasBase) {
        // Define los iconos para diferentes tipos
        val iconImages = mapOf(
            "inicio" to R.drawable.start_icon,
            "descanso" to R.drawable.stopover_icon,
            "fin" to R.drawable.end_icon
        )

        // Añade los iconos al estilo
        iconImages.forEach { (type, resId) ->
            style.addImage(type, BitmapFactory.decodeResource(resources, resId))
        }

        // Crea una lista de características con los puntos y tipos
        val features = ruta.coordenadas.map { coordenada ->
            Feature.fromGeometry(
                Point.fromLngLat(coordenada.longitud, coordenada.latitud),
                mapOf("tipo" to coordenada.tipo)
            )
        }
        val featureCollection = FeatureCollection.fromFeatures(features)

        // Elimina la fuente de símbolos existente si la hay
        val existingSymbolSource = style.getSourceAs<GeoJsonSource>("symbol-source")
        if (existingSymbolSource != null) {
            style.removeStyleSource("symbol-source")
        }

        // Crea y añade la nueva fuente con la colección de características
        val symbolSource = GeoJsonSource.Builder("symbol-source")
            .featureCollection(featureCollection)
            .build()
        style.addSource(symbolSource)

        // Añade la capa de símbolos
        val symbolLayer = SymbolLayer("symbol-layer", "symbol-source").apply {
            iconImage("{tipo}") // Usa el tipo como el nombre del icono
            iconSize(1.0)
        }
        style.addLayer(symbolLayer)
    }


}



