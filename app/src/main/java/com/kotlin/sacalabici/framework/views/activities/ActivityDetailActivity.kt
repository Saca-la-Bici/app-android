package com.kotlin.sacalabici.framework.adapters.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.mapbox.maps.plugin.annotation.annotations
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch




class DetailActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var startButton: Button
    private lateinit var ocultarButton: Button
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private var isTrackingLocation = false
    private var locationTrackingJob: Job? = null

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

        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

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

        // Configuración del botón "Iniciar" para comenzar o detener el bucle
        startButton = findViewById(R.id.btnStart)
        startButton.setOnClickListener {
            if (!isTrackingLocation) {
                // Comenzar a rastrear la ubicación
                isTrackingLocation = true
                startButton.text = "Detener"

                // Iniciar el bucle de actualización de ubicación en una corrutina
                locationTrackingJob = GlobalScope.launch {
                    while (isTrackingLocation) {
                        val idRodada = "idDeLaRodadaPrueba"
                        obtenerUbicacionActual(idRodada)

                        // Esperar 5 segundos antes de volver a ejecutar (puedes cambiar este valor)
                        delay(5000)
                    }
                }
            } else {
                // Detener el rastreo de ubicación
                isTrackingLocation = false
                startButton.text = "Iniciar"

                // Cancelar el trabajo de corrutina para detener el bucle
                locationTrackingJob?.cancel()
            }
        }
    }

    fun obtenerUbicacionActual(idRodada: String) {
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

                colocarMarcadorEnMapa(latitud, longitud)

                val cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(longitud, latitud)) // Longitud, Latitud
                    .zoom(15.0) // Ajusta el nivel de zoom según tus preferencias
                    .build()

                mapView.getMapboxMap().setCamera(cameraOptions)
            } else {
                // Manejar el caso donde la ubicación es nula
                Log.d("Ubicacion", "La ubicación es nula")
            }
        }
    }

    private fun colocarMarcadorEnMapa(latitud: Double, longitud: Double) {
        // Carga la imagen del icono
        val originalIconImage = BitmapFactory.decodeResource(resources, R.drawable.start_icon)

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




    private fun enviarUbicacion(latitud: Double, longitud: Double) {
        val url = "http://18.220.205.53:8080/rodadas/iniciarRodada/:idRodada" // Cambia a la URL de tu backend
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            JSONObject().apply {
                put("latitud", latitud)
                put("longitud", longitud)
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
                    if (response.isSuccessful) {
                        // Maneja la respuesta de éxito
                        Log.d("Ubicacion", "Ubicación actualizada exitosamente")
                    } else {
                        // Maneja la respuesta de error del servidor
                        Log.e("Ubicacion", "Error al actualizar ubicación: ${response.message}")
                    }
                } catch (e: Exception) {
                    // Manejar errores de la llamada
                    Log.e("Ubicacion", "Error al realizar la solicitud: ${e.message}")
                }
            }
        }
    }


}