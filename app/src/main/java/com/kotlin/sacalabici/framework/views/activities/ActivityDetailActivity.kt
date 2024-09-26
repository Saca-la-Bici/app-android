package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.mapbox.maps.MapView

class DetailActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activity_staff)

        // Ocultar el ActionBar si existe
        supportActionBar?.hide()

        // Inicializa el MapView
        mapView = findViewById(R.id.mapView)

    }

}

