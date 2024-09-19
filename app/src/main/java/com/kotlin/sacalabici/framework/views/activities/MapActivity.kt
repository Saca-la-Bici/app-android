package com.kotlin.sacalabici.framework.views.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.RutasFragment
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.services.RutasService
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import kotlinx.coroutines.launch

class MapActivity : BaseActivity() {

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
        mapView.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
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
                        .commit() // Llama a commit aquí solo una vez
                    rutasFragmentVisible = true
                } else {
                    Log.e("MapActivity", "Error al obtener la lista de rutas")
                }
            }
        }
    }
}


