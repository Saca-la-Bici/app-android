package com.kotlin.sacalabici.framework.adapters.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kotlin.sacalabici.R
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.CameraOptions
import com.mapbox.geojson.Point
import okhttp3.*
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.google.android.gms.tasks.Task
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import android.util.Log
import android.view.View
import okhttp3.MediaType.Companion.toMediaTypeOrNull



class DetailActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var ocultarButton: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val client = OkHttpClient()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activity_staff)

        // Ocultar el ActionBar si existe
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializa el MapView
        mapView = findViewById(R.id.mapView)
        val btnRuta: Button = findViewById(R.id.btnRuta)
        ocultarButton = findViewById(R.id.btnOcultarRuta) // Inicializa el botón para ocultar el mapa

        // Inicialmente ocultar el MapView
        mapView.visibility = View.GONE

        // Configurar el botón
        btnRuta.setOnClickListener {
            // Mostrar el MapView con el botón para quitarlo cuando se presione el botón "Ver Ruta"
            mapView.visibility = View.VISIBLE
            ocultarButton.visibility = View.VISIBLE

            // Establece el estilo del mapa
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

            // Configura la cámara para centrarse en Querétaro
            val queretaroLocation = CameraOptions.Builder()
                .center(Point.fromLngLat(-100.3899, 20.5884)) // Longitud, Latitud
                .zoom(12.0)
                .build()

            mapView.getMapboxMap().setCamera(queretaroLocation)
        }

        // Configurar el botón para ocultar el mapa
        ocultarButton.setOnClickListener {
            mapView.visibility = View.GONE
            ocultarButton.visibility = View.GONE
        }

        startButton = findViewById(R.id.btnStart)
        startButton.setOnClickListener{
            obtenerUbicacionActual()
        }
    }

    fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Obtener la última ubicación conocida
        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitud = location.latitude
                val longitud = location.longitude

                Log.d("UbicacionActual", "Latitud: $latitud, Longitud: $longitud")

                enviarUbicacion(latitud, longitud) // Envía la ubicación al backend
            } else {
                // Manejar el caso donde la ubicación es nula
            }
        }
    }

    private fun enviarUbicacion(latitud: Double, longitud: Double) {
        val url = "http://tu-backend-url/rodadas/iniciar/ubicacion" // Cambia a la URL de tu backend
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            JSONObject().apply {
                put("latitud", latitud)
                put("longitud", longitud)
                put("adminId", "tuAdminId") // Asegúrate de proporcionar un adminId válido
            }.toString()
        )

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Ejecutar la llamada en un hilo separado
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val response: Response = client.newCall(request).execute()
                    // Manejar la respuesta aquí
                } catch (e: Exception) {
                    // Manejar el error aquí
                }
            }
        }
    }
}