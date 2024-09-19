package com.kotlin.sacalabici.framework.views.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.adapters.RutasAdapter
import com.kotlin.sacalabici.framework.services.RutasService
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import kotlinx.coroutines.launch

class MapActivity : BaseActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
        initializeMap()

        val btnDetails: ImageButton = findViewById(R.id.btnDetails)

        // Botón para mostrar la lista de rutas
        btnDetails.setOnClickListener {
            showRutasList()
        }
    }

    private fun initializeBinding(){
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
    }

    private fun initializeMap() {
        mapView.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
            // Define the coordinates for Querétaro
            val queretaroCoordinates = Point.fromLngLat(-100.3899, 20.5888)

            // Move the camera to Querétaro
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(12.0) // Ajusta el zoom según sea necesario
                    .build()
            )
        }
    }

    private fun showRutasList() {
        lifecycleScope.launch {
            val rutasList = RutasService.getRutasList()

            if (rutasList != null) {
                // Obtener el fragmento y configurar el RecyclerView
                val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
                val recyclerView = fragment?.view?.findViewById<RecyclerView>(R.id.RVRutas)

                // Inicializar el adaptador y establecerlo en el RecyclerView
                val adapter = RutasAdapter(rutasList)
                recyclerView?.adapter = adapter
            } else {
                Log.e("MapActivity", "Error al obtener la lista de rutas")
            }
        }
    }
}
