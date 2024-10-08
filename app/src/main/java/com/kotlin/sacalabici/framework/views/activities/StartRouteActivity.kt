package com.kotlin.sacalabici.framework.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class StartRouteActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private lateinit var postLocationRequirement: ActivitiesRepository
    private lateinit var rodadaId: String
    private lateinit var loca: LocationR


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

        postLocationRequirement = ActivitiesRepository()

        // Obtener el id de la rodada de alguna fuente (por ejemplo, desde un Intent)
        rodadaId = intent.getStringExtra("RODADA_ID") ?: "default_id" // Definir el id aquí

        obtenerYdibujarRuta(rodadaId)
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


                        sendLocation(rodadaId, loca)
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

    private fun obtenerYdibujarRuta(rutaId: String) {
        lifecycleScope.launch {
            try {
                val request = okhttp3.Request.Builder()
                    .url("http://18.220.205.53:8080/rodada/obtenerUbicacion/$rutaId") // Asegúrate de usar la ID correcta en la URL
                    .build()

                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val jsonObject = JSONObject(jsonData)
                    val coordenadas = jsonObject.getJSONArray("coordenadas")

                    // Limpiar la lista antes de llenarla
                    rutaDelUsuario.clear()

                    for (i in 0 until coordenadas.length()) {
                        val punto = coordenadas.getJSONObject(i)
                        val latitud = punto.getDouble("latitud")
                        val longitud = punto.getDouble("longitud")
                        rutaDelUsuario.add(Point.fromLngLat(longitud, latitud))
                    }

                    // Llama a la función para dibujar la ruta, pasando la lista de puntos
                    dibujarRutaEstablecidaEnMapa(rutaDelUsuario)
                } else {
                    Log.e("Ruta", "Error al obtener la ruta: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Ruta", "Error en la solicitud: ${e.message}")
            }
        }
    }


    private fun dibujarRutaEstablecidaEnMapa(puntosRuta: List<Point>) {
        mapView.getMapboxMap().getStyle { style ->
            // Si el estilo ya tiene una capa de línea, la eliminamos primero
            style.removeStyleLayer("rutaEstablecidaLayer")
            style.removeStyleSource("rutaEstablecidaSource")

            // Creamos la fuente con los puntos de la ruta establecida
            val geoJsonSource = geoJsonSource("rutaEstablecidaSource") {
                feature(com.mapbox.geojson.Feature.fromGeometry(LineString.fromLngLats(puntosRuta)))
            }

            // Añadimos la fuente al estilo del mapa
            style.addSource(geoJsonSource)

            // Creamos la capa de línea y la añadimos al estilo
            val lineLayer = lineLayer("rutaEstablecidaLayer", "rutaEstablecidaSource") {
                lineColor("#FF0000") // Cambiar color a rojo
                lineWidth(4.0) // Grosor de la línea
            }

            style.addLayer(lineLayer)
        }
    }



    private fun sendLocation(id: String, loca: LocationR) {
        lifecycleScope.launch {
            try {
                // Llamar al método del repositorio para enviar la ubicación
                val resultado = postLocationRequirement.postLocation(id,loca)
                if (resultado) {
                    Log.d("Ubicacion", "Ubicación enviada exitosamenteeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                } else {
                    Log.e("Ubicacion", "Error al enviar la ubicación")
                }
            } catch (e: Exception) {
                Log.e("Ubicacion", "Error en la solicitud: ${e.message}")
            }
        }
    }
}