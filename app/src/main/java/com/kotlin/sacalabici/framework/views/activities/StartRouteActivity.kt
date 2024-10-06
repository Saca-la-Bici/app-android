package com.kotlin.sacalabici.framework.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.sacalabici.R
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class StartRouteActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager


    private var isTrackingLocation = false
    private var locationTrackingJob: Job? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val client = OkHttpClient()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    // Lista de puntos que representarán la ruta del usuario
    private val rutaDelUsuario = mutableListOf<Point>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)

        // Ocultar el ActionBar si existe
        supportActionBar?.hide()

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializa el MapView
        mapView = findViewById(R.id.mapView)
        initializeMap()

    }

    private fun initializeMap(){
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        // Centrar la cámara en una ubicación inicial (por ejemplo, Querétaro)
        val queretaroLocation = CameraOptions.Builder()
            .center(Point.fromLngLat(-100.3899, 20.5884)) // Longitud, Latitud
            .zoom(12.0)
            .build()
        mapView.getMapboxMap().setCamera(queretaroLocation)

        enableLocationComponent()
    }

    private fun enableLocationComponent() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        with(mapView) {
            location.enabled = true
            location.puckBearing = PuckBearing.COURSE
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                // Mover la cámara a la ubicación actual del usuario y hacer zoom
                val userLocation = CameraOptions.Builder()
                    .center(Point.fromLngLat(it.longitude, it.latitude)) // Usar la ubicación del usuario
                    .zoom(15.0) // Nivel de zoom más cercano
                    .build()
                mapView.getMapboxMap().setCamera(userLocation)

                // Comenzar a seguir la ubicación del usuario
                iniciarSeguimientoDeUbicacion()
            }
        }
    }

    private fun iniciarSeguimientoDeUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Configura actualizaciones periódicas de la ubicación con LocationRequest.Builder
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).setMinUpdateIntervalMillis(2000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val punto = Point.fromLngLat(location.longitude, location.latitude)
                        // Agregar el punto actual a la lista de la ruta
                        rutaDelUsuario.add(punto)
                        // Dibujar la línea en el mapa
                        dibujarLineaEnMapa()
                    }
                }
            },
            null
        )
    }


    private fun dibujarLineaEnMapa() {
        mapView.getMapboxMap().getStyle { style ->
            // Si el estilo ya tiene una capa de línea, la eliminamos primero
            style.removeStyleLayer("rutaDelUsuarioLayer")
            style.removeStyleSource("rutaDelUsuarioSource")

            // Creamos la fuente con los puntos de la ruta
            val geoJsonSource = geoJsonSource("rutaDelUsuarioSource") {
                feature(com.mapbox.geojson.Feature.fromGeometry(LineString.fromLngLats(rutaDelUsuario)))
            }

            // Añadimos la fuente al estilo del mapa
            style.addSource(geoJsonSource)

            // Creamos la capa de línea y la añadimos al estilo
            val lineLayer = lineLayer("rutaDelUsuarioLayer", "rutaDelUsuarioSource") {
                lineColor("#0000FF") // Cambiar color a azul
                lineWidth(4.0) // Grosor de la línea
            }

            style.addLayer(lineLayer)
        }
    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun enviarUbicacion(latitud: Double, longitud: Double) {
        val url = "http://18.220.205.53:8080/rodadas/iniciarRodada/:idRodada"
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            JSONObject().apply {
                put("latitud", latitud)
                put("longitud", longitud)
            }.toString()
        )

        // Ejecutar la llamada en un hilo separado
        GlobalScope.launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("Ubicacion", "Ubicación actualizada exitosamente")
                } else {
                    Log.e("Ubicacion", "Error al actualizar ubicación: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Ubicacion", "Error al realizar la solicitud: ${e.message}")
            }
        }
    }
}
