package com.kotlin.sacalabici.framework.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository
import com.kotlin.sacalabici.databinding.ActivityLookRouteBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.helpers.MapHelper
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.location

class LookRouteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLookRouteBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_route)
        initializeBinding()

        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)

        val extras = intent.extras
        val id = extras?.getString("ID") ?: ""

        val mapHelper = MapHelper(this)

        // Observa los cambios en el LiveData del ViewModel
        // Primero, observamos ambos LiveData en el mismo bloque
        viewModel.rodadaInfo.observe(this) { rodadaInfo ->
            // Lógica para `rodadaInfo`
            rodadaInfo?.let { it ->
                rodadaInfoData = it // Guarda la información en la variable
                Log.d("Rodada", "Información de la rodada recibida: $rodadaInfoData")

                rodadaId = rodadaInfoData?.rodadaId
                rutaId = rodadaInfoData?.rutaId

                Log.d("Rodada", "Id de la rodada recibida: $rodadaId")
                Log.d("Rodada", "Id de la ruta recibida: $rutaId")

                rutaId?.let { it1 -> viewModel.getRoute(it1) }

                // En tu onCreate
                val handler = Handler(mainLooper)
                val runnable =
                    object : Runnable {
                        override fun run() {
                            // Llamar a la función para obtener la ubicación
                            rodadaId?.let { it1 -> viewModel.getLocation(it1) } // `id` es el que has recibido en el intent
                            handler.postDelayed(this, 2000) // Llamar a esta función cada 2 segundos
                        }
                    }

                handler.post(runnable)
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

        // En tu onCreate o en el lugar adecuado
        viewModel.locationInfo.observe(this) { locationList ->
            // Lógica para agregar el último punto de la lista a la ruta
            locationList?.let {
                if (it.isNotEmpty()) {
                    // Agregar el último punto de la lista de ubicaciones
                    val lastLocation = it.last()
                    Log.d("Ubicacion", "Ultima ubicacion: $lastLocation")
                    val point = Point.fromLngLat(lastLocation.longitude, lastLocation.latitude)
                    rutaDelUsuario.add(point)
                    dibujarLineaEnMapa() // Redibujar la ruta con el nuevo punto
                }
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
        binding = ActivityLookRouteBinding.inflate(layoutInflater)
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

                // Agregar icono de ubicación del usuario
                mapView.getMapboxMap().getStyle { style ->
                    if (!style.styleLayerExists("user-location-layer")) {
                        style.addImage(
                            "user-location-icon",
                            BitmapFactory.decodeResource(resources, R.drawable.location_icon)
                        )
                    }

                    style.addSource(
                        geoJsonSource("user-location-source") {
                            feature(
                                com.mapbox.geojson.Feature.fromGeometry(
                                    Point.fromLngLat(it.longitude, it.latitude)
                                )
                            )
                        }
                    )

                    style.addLayer(
                        symbolLayer("user-location-layer", "user-location-source") {
                            iconImage("user-location-icon")
                            iconAllowOverlap(true)
                            iconIgnorePlacement(true)
                        }
                    )
                }

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
                }
            },
            null,
        )
    }

    private fun dibujarLineaEnMapa() {
        mapView.mapboxMap.getStyle { style ->
            style.removeStyleLayer("rutaDelUsuarioLayer")
            style.removeStyleSource("rutaDelUsuarioSource")
            style.removeStyleLayer("caminoIconsLayer")
            style.removeStyleSource("caminoIconsSource")

            val geoJsonSource =
                geoJsonSource("rutaDelUsuarioSource") {
                    feature(
                        com.mapbox.geojson.Feature.fromGeometry(LineString.fromLngLats(rutaDelUsuario)),
                    )
                }

            style.addSource(geoJsonSource)

            val lineLayer =
                lineLayer("rutaDelUsuarioLayer", "rutaDelUsuarioSource") {
                    lineColor("#0000FF") // Color de la línea
                    lineWidth(4.0) // Grosor de la línea
                }

            style.addLayer(lineLayer)

            if (!style.styleLayerExists("custom-waypoint-icon")) {
                style.addImage(
                    "custom-waypoint-icon",
                    BitmapFactory.decodeResource(resources, R.drawable.waypoint_icon),
                )
            }

            // Agregar el ícono solo en el último punto de la ruta
            val lastPoint = rutaDelUsuario.lastOrNull()
            if (lastPoint != null) {
                style.addSource(
                    geoJsonSource("caminoIconsSource") {
                        feature(
                            com.mapbox.geojson.Feature.fromGeometry(lastPoint)
                        )
                    }
                )

                val caminoIconsLayer =
                    symbolLayer("caminoIconsLayer", "caminoIconsSource") {
                        iconImage("custom-waypoint-icon")
                        iconAllowOverlap(true)
                        iconIgnorePlacement(true)
                        iconAnchor(IconAnchor.CENTER)
                    }
                style.addLayer(caminoIconsLayer)
            }
        }
    }

}
