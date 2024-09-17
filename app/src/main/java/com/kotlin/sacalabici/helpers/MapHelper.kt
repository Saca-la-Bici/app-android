package com.kotlin.sacalabici.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.R
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MapHelper: AppCompatActivity() {


    private lateinit var mapView: MapView
    var startPoint: Point? = null
    var stopoverPoint: Point? = null
    private var stopoverPoint2: Point? = null
    private var stopoverPoint3: Point? = null
    var endPoint: Point? = null

    private lateinit var mapViewForm: MapView
    private lateinit var etDistancia: EditText
    private lateinit var tvNivel: TextView

    fun initializeMap(map: MapView, Distancia: EditText) {
        // Inicialización del mapa
        map.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
            val queretaroCoordinates = Point.fromLngLat(-100.3899, 20.5888)
            map.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(12.0)
                    .build()
            )
            enableLocationComponent(map)
            setupMapLongClickListener(map, Distancia)
        }
    }

    private fun enableLocationComponent(map: MapView) {
        map.location.enabled = true
    }

    private fun setupMapLongClickListener(map: MapView, Distancia: EditText) {
        // Agregar marcador en el mapa cuando el usuario haga click largo

        map.getMapboxMap().addOnMapLongClickListener { point ->

            val context = map.context ?: return@addOnMapLongClickListener false

            when {
                startPoint == null -> {
                    startPoint = point
                    Toast.makeText(context, "Punto de inicio establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "start-point-symbol", "start_icon", map)
                }
                stopoverPoint == null -> {
                    stopoverPoint = point
                    Toast.makeText(context, "Punto de descanso establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "stopover-point-symbol", "stopover_icon", map)
                }
                endPoint == null -> {
                    endPoint = point
                    Toast.makeText(context, "Punto final establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "end-point-symbol", "end_icon", map)
                    drawRoute(map, Distancia)
                }
                else -> {
                    Toast.makeText(this, "Ya se han establecido todos los puntos.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun drawRoute(map:MapView, etDistancia: EditText) {
        val points = listOfNotNull(startPoint, stopoverPoint, stopoverPoint2, stopoverPoint3, endPoint)
        if (points.size < 2) {
            Toast.makeText(this,"Establezca al menos dos puntos para crear la ruta.", Toast.LENGTH_SHORT).show()
            return
        }

        val origin = points.first()
        val destination = points.last()
        val waypoints = points.subList(1, points.size - 1)

        val waypointsCoordinates = waypoints.joinToString(";") { "${it.longitude()},${it.latitude()}" }

        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${origin.longitude()},${origin.latitude()};$waypointsCoordinates;${destination.longitude()},${destination.latitude()}?geometries=geojson&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")


        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val geometry = route.getJSONObject("geometry")
                    val lineString = LineString.fromJson(geometry.toString())
                    val distance = route.getDouble("distance") / 1000.0

                    withContext(Dispatchers.Main) {
                        etDistancia.setText(String.format("%.2f km", distance))

                        val routeSource = geoJsonSource("route-source") {
                            geometry(lineString)
                        }
                        map.getMapboxMap().getStyle { style ->
                            style.addSource(routeSource)

                            val routeLayer = lineLayer("route-layer", "route-source") {
                                lineWidth(5.0)
                            }
                            style.addLayer(routeLayer)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AgregarRutaActivity", "Error al obtener la ruta: ${e.message}")
            }
        }
    }

    private fun addMarker(point: Point, symbolId: String, iconName: String, map: MapView) {
        // Asegurarse de que el MapView tiene un contexto válido
        val context = map.context ?: map.context.applicationContext ?: return

        map.getMapboxMap().getStyle { style ->
            // Verificar si la imagen ya ha sido añadida al estilo
            if (style.getStyleImage(iconName) == null) {
                // Usar el contexto seguro para acceder a los recursos
                val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                if (iconResId != 0) {
                    val icon = BitmapFactory.decodeResource(context.resources, iconResId)
                    style.addImage(iconName, icon)
                } else {
                    // Si no se encuentra el recurso de imagen, salir de la función
                    return@getStyle
                }
            }

            // Crear la fuente GeoJSON para el marcador
            val source = geoJsonSource(symbolId) {
                geometry(point)
            }

            // Verificar si la fuente ya existe antes de agregarla
            if (!style.styleSourceExists(symbolId)) {
                style.addSource(source)
            }

            // Ajuste del offset en función del icono específico
            val offset = when (iconName) {
                "start_icon" -> listOf(0.0, -10.0) // Desplazar hacia arriba
                "end_icon" -> listOf(0.0, -10.0)   // Desplazar hacia arriba
                else -> listOf(0.0, 0.0)           // Sin desplazamiento para otros íconos
            }

            // Crear la capa del símbolo con la imagen específica
            val symbolLayer = symbolLayer(symbolId + "-layer", symbolId) {
                iconImage(iconName)  // Usar el nombre del ícono
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconSize(0.07)
                iconAnchor(IconAnchor.BOTTOM) // Ancla el ícono en la parte inferior
                iconOffset(offset) // Aplicar el offset basado en el ícono
            }

            // Verificar si la capa ya existe antes de agregarla
            if (!style.styleLayerExists(symbolId + "-layer")) {
                style.addLayer(symbolLayer)
            }
        }
    }



    fun getRoutePoints(): List<Point> = listOfNotNull(startPoint, stopoverPoint, endPoint)

    fun isRouteComplete(): Boolean = startPoint != null && stopoverPoint != null && endPoint != null
}
