package com.kotlin.sacalabici.framework.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository
import com.kotlin.sacalabici.databinding.ActivityStartRouteBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.helpers.MapHelper
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
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
    private var coordenadasRuta: List<CoordenatesBase>? = null
    private var isTracking = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)
        initializeBinding()

        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)

        val extras = intent.extras
        val id = extras?.getString("ID") ?: ""

        Log.d("Actividad", "ID de la actividad: $id")

        var mapHelper = MapHelper(this)

        binding.stopTrackingButton.setOnClickListener {
            isTracking = !isTracking // Cambia entre iniciar y detener cuando pinta la ruta
            if (isTracking) {
                binding.stopTrackingButton.text = "Detener Ruta"
            } else {
                binding.stopTrackingButton.text = "Iniciar Ruta"
            }
        }

        // Observa los cambios en el LiveData del ViewModel
        // Primero, observamos ambos LiveData en el mismo bloque
        viewModel.rodadaInfo.observe(this) { rodadaInfo ->
            // Lógica para `rodadaInfo`
            rodadaInfo?.let { it ->
                rodadaInfoData = it // Guarda la información en la variable
                rodadaId = rodadaInfoData?.rodadaId
                rutaId = rodadaInfoData?.rutaId
                rutaId?.let { it1 -> viewModel.getRoute(it1) }
            }
        }

        viewModel.routeInfo.observe(this) { routeInfo ->
            routeInfoData = routeInfo
            routeInfoData?.ruta?.coordenadas?.let { coordenadas ->

                val coordenadasJson = Gson().toJson(routeInfoData?.ruta?.coordenadas)
                val coordenadasType = object : TypeToken<ArrayList<CoordenatesBase>>() {}.type
                val coordenadasList: ArrayList<CoordenatesBase> = Gson().fromJson(coordenadasJson, coordenadasType)

                mapHelper.drawRouteWithCoordinates(mapView, coordenadasList)
            }
        }

        // Solicita la información de la rodada desde el ViewModel
        viewModel.getRodadaInfo(id)

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
        val queretaroLocation =
            CameraOptions
                .Builder()
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
                val userLocation =
                    CameraOptions
                        .Builder()
                        .center(Point.fromLngLat(it.longitude, it.latitude)) // Usar la ubicación del usuario
                        .zoom(15.0) // Nivel de zoom más cercano
                        .build()
                mapView.getMapboxMap().setCamera(userLocation)

                // Crear el objeto LocationR con la ubicación del usuario
                val userLocationR =
                    LocationR(
                        id = "uniqueIdForLocation", // Asigna un ID único o generado
                        latitude = it.latitude,
                        longitude = it.longitude,
                    )

                // Enviar la ubicación al servidor
                rodadaId?.let {
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

        val locationRequest =
            com.google.android.gms.location.LocationRequest
                .Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    5000L,
                ).setMinUpdateIntervalMillis(2000L)
                .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    if (isTracking) {
                        locationResult.lastLocation?.let { location ->
                            val punto = Point.fromLngLat(location.longitude, location.latitude)
                            rutaDelUsuario.add(punto)
                            dibujarLineaEnMapa()

                            val updatedUserLocationR = LocationR(
                                id = "uniqueIdForLocation",  // Asigna un ID único o generado
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                            // Enviar la ubicación actualizada al servidor
                            rodadaId?.let {
                                viewModel.postUbicacion(it, updatedUserLocationR)
                            }
                        }
                    }
                }
            },
            null,
        )
    }

    private fun dibujarLineaEnMapa() {
        mapView.getMapboxMap().getStyle { style ->
            // Si el estilo ya tiene una capa de línea, la eliminamos primero
            style.removeStyleLayer("rutaDelUsuarioLayer")
            style.removeStyleSource("rutaDelUsuarioSource")

            // Creamos la fuente con los puntos de la ruta
            val geoJsonSource =
                geoJsonSource("rutaDelUsuarioSource") {
                    feature(
                        com.mapbox.geojson.Feature
                            .fromGeometry(LineString.fromLngLats(rutaDelUsuario)),
                    )
                }

            // Añadimos la fuente al estilo del mapa
            style.addSource(geoJsonSource)

            // Creamos la capa de línea y la añadimos al estilo
            val lineLayer =
                lineLayer("rutaDelUsuarioLayer", "rutaDelUsuarioSource") {
                    lineColor("#0000FF") // Cambiar color a azul
                    lineWidth(4.0) // Grosor de la línea
                }

            style.addLayer(lineLayer)
        }
    }
}
