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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.sacalabici.R
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)

        // Ocultar el ActionBar si existe
        supportActionBar?.hide()

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializa el MapView
        mapView = findViewById(R.id.mapView)
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        // Configurar el botón "Iniciar"
        if (!isTrackingLocation) {
            // Comenzar a rastrear la ubicación
            isTrackingLocation = true

            // Iniciar el bucle de actualización de ubicación en una corrutina
            locationTrackingJob = GlobalScope.launch {
                while (isTrackingLocation) {
                    val idRodada = "idDeLaRodadaPrueba"
                    obtenerUbicacionActual()
                    delay(5000) // Esperar 5 segundos antes de volver a ejecutar
                }
            }
        } else {
            // Detener el rastreo de ubicación
            isTrackingLocation = false
            locationTrackingJob?.cancel()
        }

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        // Centrar la cámara en una ubicación inicial (por ejemplo, Querétaro)
        val queretaroLocation = CameraOptions.Builder()
            .center(Point.fromLngLat(-100.3899, 20.5884)) // Longitud, Latitud
            .zoom(12.0)
            .build()
        mapView.getMapboxMap().setCamera(queretaroLocation)
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Obtener la última ubicación conocida
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitud = location.latitude
                val longitud = location.longitude

                Log.d("UbicacionActual", "Latitud: $latitud, Longitud: $longitud")
                enviarUbicacion(latitud, longitud)
                colocarMarcadorEnMapa(latitud, longitud)

                // Mover la cámara a la ubicación actual
                val cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(longitud, latitud))
                    .zoom(15.0)
                    .build()
                mapView.getMapboxMap().setCamera(cameraOptions)
            } else {
                Log.d("Ubicacion", "La ubicación es nula")
            }
        }
    }

    private fun colocarMarcadorEnMapa(latitud: Double, longitud: Double) {
        // Carga la imagen del icono
        val originalIconImage = BitmapFactory.decodeResource(resources, R.drawable.blue_dot)

        // Redimensiona el icono a un tamaño fijo, por ejemplo, 50x50 píxeles
        val resizedIconImage = Bitmap.createScaledBitmap(originalIconImage, 50, 50, false)

        // Añade la imagen al mapa con un ID único
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            mapView.getMapboxMap().getStyle { style ->
                style.addImage("iconImage", resizedIconImage) // Usa el icono redimensionado
            }
        }

        // Crear una nueva anotación con las coordenadas
        val annotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(longitud, latitud)) // Longitud, Latitud
            .withIconImage("iconImage") // Usa el mismo ID que usaste en addImage

        // Añadir la anotación al mapa
        pointAnnotationManager.create(annotationOptions)
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
