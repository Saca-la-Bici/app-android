package com.kotlin.sacalabici.framework.views.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
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
                // Crear el fragmento pasando la lista de rutas como argumento
                val fragment = RutasFragment.newInstance(rutasList)

                // Reemplaza el contenido del contenedor de fragmentos con el nuevo fragmento
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, fragment) // Cambia el ID por el de tu contenedor
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.e("MapActivity", "Error al obtener la lista de rutas")
            }
        }
    }


}
