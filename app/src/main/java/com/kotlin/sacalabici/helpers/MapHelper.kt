/**
 * File: MapHelper.kt
 * Description: Esta clase maneja la interacción con Mapbox, permitiendo al usuario establecer
 *              puntos de inicio, descanso y final en un mapa, agregar marcadores visuales para
 *              cada punto, y trazar una ruta entre ellos utilizando la API de direcciones de Mapbox.
 *              Además, calcula y muestra la distancia de la ruta en kilómetros.
 * Date: 18/09/2024
 * Changes:
 */

package com.kotlin.sacalabici.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.BuildConfig
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

class MapHelper(private val context: Context) : AppCompatActivity() {


    // Vista del mapa y campo de texto para la distancia
    private lateinit var mapView: MapView
    private lateinit var etDistancia: EditText

    // Puntos en el mapa: inicio, parada intermedia y final
    var startPoint: Point? = null
    var stopoverPoint: Point? = null
    var endPoint: Point? = null

    // Callbacks para los puntos: acciones cuando se establecen los puntos
    private var onStartPointSet: ((Point) -> Unit)? = null
    private var onStopoverPointSet: ((Point) -> Unit)? = null
    private var onEndPointSet: ((Point) -> Unit)? = null


    /**
     * Inicializa el mapa con los elementos de la vista y los callbacks para los puntos.
     * Configura el estilo del mapa y establece un centro inicial en Querétaro.
     *
     * @param map Vista del mapa a inicializar
     * @param distancia Campo de texto donde se mostrará la distancia
     * @param onStartPointSet Callback cuando se selecciona el punto de inicio
     * @param onStopoverPointSet Callback cuando se selecciona el punto de parada
     * @param onEndPointSet Callback cuando se selecciona el punto final
     */


    fun initializeMap(map: MapView, distancia: EditText, onStartPointSet: (Point) -> Unit, onStopoverPointSet: (Point) -> Unit, onEndPointSet: (Point) -> Unit) {
        // Asigna las vistas y callbacks proporcionadas
        this.mapView = map
        this.etDistancia = distancia
        this.onStartPointSet = onStartPointSet
        this.onStopoverPointSet = onStopoverPointSet
        this.onEndPointSet = onEndPointSet

        // Carga el estilo de Mapbox y centra el mapa en Querétaro
        map.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
            val queretaroCoordinates = Point.fromLngLat(-100.4091, 20.5925)
            map.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates) // Centro en Querétaro
                    .zoom(15.0) // Nivel de zoom inicial
                    .build()
            )

            // Habilita el componente de localización
            enableLocationComponent(map)

            // Configura los listeners de clicks largos para establecer puntos
            setupMapLongClickListener(map,distancia,onStartPointSet,onStopoverPointSet,onEndPointSet)
        }
    }

    /**
     * Habilita el componente de localización en el mapa.
     *
     * @param map Vista del mapa donde se habilitará la localización.
     */
    private fun enableLocationComponent(map: MapView) {
        map.location.enabled = true
    }


    /**
     * Configura el listener para los clics largos en el mapa.
     * Dependiendo del estado, establece puntos de inicio, parada y final.
     * Muestra mensajes al usuario y agrega marcadores visuales para los puntos.
     *
     * @param map Vista del mapa donde se establecerán los puntos.
     * @param distancia Campo de texto donde se mostrará la distancia.
     * @param onStartPointSet Callback cuando se selecciona el punto de inicio.
     * @param onStopoverPointSet Callback cuando se selecciona el punto de descanso.
     * @param onEndPointSet Callback cuando se selecciona el punto final.
     */
    private fun setupMapLongClickListener(
        map: MapView,
        distancia: EditText,
        onStartPointSet: (Point) -> Unit,
        onStopoverPointSet: (Point) -> Unit,
        onEndPointSet: (Point) -> Unit
    ) {
        // Agrega un listener para detectar clics largos en el mapa
        map.getMapboxMap().addOnMapLongClickListener { point ->

            // Si el contexto es nulo, no continúa
            val context = map.context ?: return@addOnMapLongClickListener false

            // Verifica cuál punto debe ser establecido: inicio, descanso o final
            when {
                startPoint == null -> {

                    // Establece el punto de inicio
                    startPoint = point
                    onStartPointSet(point)  // Llama al callback para el punto de inicio
                    Toast.makeText(context, "Punto de inicio establecido.", Toast.LENGTH_SHORT).show()

                    // Agrega un marcador en el mapa para el punto de inicio
                    addMarker(point, "start-point-symbol", "start_icon", map)
                }
                stopoverPoint == null -> {

                    // Establece el punto de descanso
                    stopoverPoint = point
                    onStopoverPointSet(point)  // Llama al callback para el punto de descanso
                    Toast.makeText(context, "Punto de descanso establecido.", Toast.LENGTH_SHORT).show()

                    // Agrega un marcador en el mapa para el punto de descanso
                    addMarker(point, "stopover-point-symbol", "stopover_icon", map)
                }
                endPoint == null -> {

                    // Establece el punto final
                    endPoint = point
                    onEndPointSet(point)  // Llama al callback para el punto final
                    Toast.makeText(context, "Punto final establecido.", Toast.LENGTH_SHORT).show()

                    // Agrega un marcador en el mapa para el punto final
                    addMarker(point, "end-point-symbol", "end_icon", map)

                    // Dibuja la ruta entre los puntos establecidos
                    drawRoute(map, distancia)
                }
                else -> {

                    // Si ya están establecidos todos los puntos, muestra un mensaje
                    Log.d("MapPoints", "Start: $startPoint, Stopover: $stopoverPoint, End: $endPoint")
                    Toast.makeText(context, "Ya se han establecido todos los puntos.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }


    /**
     * Traza una ruta en el mapa utilizando la API de direcciones de Mapbox.
     * Se requieren al menos dos puntos (inicio y destino) para trazar la ruta.
     * La distancia calculada se muestra en el campo de texto de distancia.
     *
     * @param map Vista del mapa donde se dibujará la ruta.
     * @param etDistancia Campo de texto donde se mostrará la distancia calculada.
     */
    private fun drawRoute(map: MapView, etDistancia: EditText) {
        // Lista de puntos no nulos: inicio, parada intermedia y final
        val points = listOfNotNull(startPoint, stopoverPoint, endPoint)

        // Verifica si hay al menos dos puntos para trazar la ruta
        if (points.size < 2) {
            Toast.makeText(context, "Establezca al menos dos puntos para crear la ruta.", Toast.LENGTH_SHORT).show()
            return
        }

        // Punto de origen (primer punto) y destino (último punto)
        val origin = points.first()
        val destination = points.last()

        // Puntos intermedios entre origen y destino (si los hay)
        val waypoints = points.subList(1, points.size - 1)

        // Formatea las coordenadas de los puntos intermedios
        val waypointsCoordinates = waypoints.joinToString(";") { "${it.longitude()},${it.latitude()}" }

        // URL de la solicitud a la API de direcciones de Mapbox
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${origin.longitude()},${origin.latitude()};$waypointsCoordinates;${destination.longitude()},${destination.latitude()}?geometries=geojson&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        // Lanza una coroutine para realizar la solicitud en segundo plano
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Abre la conexión HTTP y obtiene la respuesta
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Procesa la respuesta JSON
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")

                if (routes.length() > 0) {
                    // Obtiene la primera ruta y sus detalles (geometría y distancia)
                    val route = routes.getJSONObject(0)
                    val geometry = route.getJSONObject("geometry")
                    val lineString = LineString.fromJson(geometry.toString())
                    val distance = route.getDouble("distance") / 1000.0  // Distancia en kilómetros

                    withContext(Dispatchers.Main) {
                        // Muestra la distancia en el campo de texto
                        etDistancia.setText(String.format("%.2f km", distance))

                        // Crea y agrega la fuente para la ruta en el mapa
                        val routeSource = geoJsonSource("route-source") {
                            geometry(lineString)
                        }
                        map.getMapboxMap().getStyle { style ->
                            style.addSource(routeSource)

                            // Crea y agrega la capa de la ruta en el mapa
                            val routeLayer = lineLayer("route-layer", "route-source") {
                                lineWidth(5.0)  // Grosor de la línea de la ruta
                            }
                            style.addLayer(routeLayer)
                        }
                    }
                }
            } catch (e: Exception) {
                // Muestra un mensaje de error en caso de fallo
                Log.e("AgregarRutaActivity", "Error al obtener la ruta: ${e.message}")
            }
        }
    }

    /**
     * Agrega un marcador en el mapa en la ubicación especificada.
     * Los íconos pueden variar dependiendo de si es un punto de inicio, parada intermedia o final.
     *
     * @param point Coordenadas del punto donde se agregará el marcador.
     * @param symbolId Identificador del símbolo (para la fuente de datos en el mapa).
     * @param iconName Nombre del ícono a mostrar para el marcador.
     * @param map Vista del mapa donde se agregará el marcador.
     */


    private fun addMarker(point: Point, symbolId: String, iconName: String, map: MapView) {
        val context = map.context ?: map.context.applicationContext ?: return // Obtiene el contexto de la vista del mapa

        // Accede al estilo del mapa y verifica si el ícono ya está cargado
        map.getMapboxMap().getStyle { style ->
            if (style.getStyleImage(iconName) == null) {
                val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                if (iconResId != 0) {
                    val icon = BitmapFactory.decodeResource(context.resources, iconResId)
                    style.addImage(iconName, icon)
                } else {
                    return@getStyle
                }
            }

            // Crea la fuente de datos para el marcador (coordenadas)
            val source = geoJsonSource(symbolId) {
                geometry(point)
            }

            // Agrega la fuente si no existe ya en el estilo del mapa
            if (!style.styleSourceExists(symbolId)) {
                style.addSource(source)
            }

            val offset = when (iconName) {
                "start_icon" -> listOf(0.0, -10.0)
                "end_icon" -> listOf(0.0, -10.0)
                else -> listOf(0.0, 0.0)
            }

            // Crea la capa de símbolo para mostrar el ícono del marcador
            val symbolLayer = symbolLayer(symbolId + "-layer", symbolId) {
                iconImage(iconName)
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconSize(0.07)
                iconAnchor(IconAnchor.BOTTOM)
                iconOffset(offset)
            }

            if (!style.styleLayerExists(symbolId + "-layer")) {
                style.addLayer(symbolLayer)
            }
        }
    }
}

