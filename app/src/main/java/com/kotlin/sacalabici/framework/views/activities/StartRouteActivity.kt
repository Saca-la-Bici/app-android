package com.kotlin.sacalabici.framework.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository
import com.kotlin.sacalabici.databinding.ActivityStartRouteBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.location

class StartRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartRouteBinding
    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var mapView: MapView
    private lateinit var postLocationRequirement: ActivitiesRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private var rodadaInfoData: RodadaInfoBase? = null
    private var routeInfoData: RouteInfoObjectBase? = null
    private val rutaDelUsuario = mutableListOf<Point>()
    private var rodadaId: String? = null
    private var rutaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)
        initializeBinding()

        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)

        val extras = intent.extras
        val id = extras?.getString("ID") ?: ""

        // Observa los cambios en el LiveData del ViewModel
        viewModel.rodadaInfo.observe(this) { rodadaInfo ->
            rodadaInfo?.let {
                rodadaInfoData = it // Guarda la información en la variable
                Log.d("Rodada", "Información de la rodada recibida: $rodadaInfoData")

                rodadaId = rodadaInfoData?.rodadaId
                rutaId = rodadaInfoData?.rutaId

                Log.d("Rodada", "Id de la rodada recibida: $rodadaId")
                Log.d("Rodada", "Id de la ruta recibida: $rutaId")
            }
        }

        viewModel.routeInfo.observe(this) { routeInfo ->
            routeInfo?.let {
                routeInfoData = it
                Log.d("Ruta", "Información de la ruta recibida: $routeInfoData")
                Log.d("Ruta", "Coordenadas: ${routeInfoData!!.ruta.coordenadas}")
            }
        }

        // Solicita la información de la rodada desde el ViewModel
        viewModel.getRodadaInfo(id)

        rutaId?.let { viewModel.getRoute(it) }



        // Ocultar el ActionBar si existe
        supportActionBar?.hide()

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializa el MapView
        mapView = findViewById(R.id.mapView)
        initializeMap()

        postLocationRequirement = ActivitiesRepository()
    }

    private fun initializeBinding() {
        binding = ActivityStartRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeMap() {
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

                // Crear el objeto LocationR con la ubicación del usuario
                val userLocationR = LocationR(
                    id = "uniqueIdForLocation",  // Asigna un ID único o generado
                    latitude = it.latitude,
                    longitude = it.longitude
                )

                // Agregar log antes de enviar la ubicación
                Log.d("Location", "Ubicación obtenida: Latitud = ${it.latitude}, Longitud = ${it.longitude}")

                // Enviar la ubicación al servidor
                rodadaId?.let {
                    Log.d("Location", "Enviando ubicación a servidor para rodadaId = $rodadaId")
                    viewModel.postUbicacion(it, userLocationR)
                }

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

                        // Crear el objeto LocationR con la nueva ubicación del usuario
                        val updatedUserLocationR = LocationR(
                            id = "uniqueIdForLocation",  // Asigna un ID único o generado
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                        // Agregar log antes de enviar la ubicación
                        Log.d("Location", "Ubicación actualizada: Latitud = ${location.latitude}, Longitud = ${location.longitude}")

                        // Enviar la ubicación actualizada al servidor
                        rodadaId?.let {
                            Log.d("Location", "Enviando ubicación actualizada al servidor para rodadaId = $rodadaId")
                            viewModel.postUbicacion(it, updatedUserLocationR)
                        }
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
}
