package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.adapters.RutasAdapter
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapActivity: BaseActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView

    private val adapter : RutasAdapter = RutasAdapter()
    private lateinit var data:ArrayList<RutasBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
        initializeMap()
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
}