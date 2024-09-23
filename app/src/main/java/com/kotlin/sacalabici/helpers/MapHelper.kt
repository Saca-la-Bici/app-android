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
import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
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

    // Para almacenar las fuentes y capas de rutas
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    // Para almacenar las fuentes y capas de pines
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

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
        // Verificamos que los puntos están establecidos
        if (startPoint == null || endPoint == null || stopoverPoint == null) {
            Toast.makeText(context, "Asegúrate de establecer los puntos de inicio, descanso y final.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lista de puntos: inicio, descanso y final
        val points = listOf(startPoint!!, stopoverPoint!!, endPoint!!)

        // URL de la solicitud para la API de Mapbox
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${points[0].longitude()},${points[0].latitude()};${points[1].longitude()},${points[1].latitude()};${points[2].longitude()},${points[2].latitude()}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        // Lanza una coroutine para realizar la solicitud de la ruta
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
                    val geometry = route.getString("geometry") // Aquí obtenemos la cadena en formato polyline6
                    val decodedPoints = decodePolyline(geometry) // Decodifica la polyline6 en coordenadas
                    val distance = route.getDouble("distance") / 1000.0

                    // Divide los puntos en tramos según la separación deseada
                    val tramo1 = decodedPoints.takeWhile { it.latitude() <= stopoverPoint!!.latitude() }
                    val tramo2 = decodedPoints.dropWhile { it.latitude() <= stopoverPoint!!.latitude() }

                    withContext(Dispatchers.Main) {
                        etDistancia.setText(String.format("%.2f km", distance))

                        map.getMapboxMap().getStyle { style ->
                            // Agrega una capa roja para el tramo inicio -> descanso
                            if (tramo1.isNotEmpty()) {
                                val sourceInicioDescanso = geoJsonSource("route-source-inicio-descanso") {
                                    geometry(LineString.fromLngLats(tramo1))
                                }
                                style.addSource(sourceInicioDescanso)
                                val layerInicioDescanso = lineLayer("route-layer-inicio-descanso", "route-source-inicio-descanso") {
                                    lineColor("#FF0000") // Rojo
                                    lineWidth(5.0)
                                }
                                style.addLayer(layerInicioDescanso)
                            }

                            // Agrega una capa verde para el tramo descanso -> final
                            if (tramo2.isNotEmpty()) {
                                val sourceDescansoFinal = geoJsonSource("route-source-descanso-final") {
                                    geometry(LineString.fromLngLats(tramo2))
                                }
                                style.addSource(sourceDescansoFinal)
                                val layerDescansoFinal = lineLayer("route-layer-descanso-final", "route-source-descanso-final") {
                                    lineColor("#228B22") // Verde
                                    lineWidth(5.0)
                                }
                                style.addLayer(layerDescansoFinal)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("drawRoute", "Error al obtener la ruta: ${e.message}")
            }
        }
    }


    // Función para decodificar polyline6 en una lista de puntos
    private fun decodePolyline(encodedPolyline: String): List<Point> {
        return PolylineUtils.decode(encodedPolyline, 6).map { Point.fromLngLat(it.longitude(), it.latitude()) }
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

    fun drawRouteWithCoordinates(map: MapView, coordenadas: List<CoordenadasBase>) {

        this.mapView = map
        // Lista de puntos: inicio, descanso y final
        val points = coordenadas.map { Point.fromLngLat(it.longitud, it.latitud) }
        val startPoint = points.first()
        val stopoverPoint = points[1]
        val endPoint = points.last()

        map.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
            map.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(startPoint) // Centro en Querétaro
                    .zoom(15.0) // Nivel de zoom inicial
                    .build()
            )
        }

        if (coordenadas.size < 3) {
            Toast.makeText(mapView.context, "Asegúrate de establecer los puntos de inicio, descanso y final.", Toast.LENGTH_SHORT).show()
            return
        }

        // URL de la solicitud a la API de direcciones de Mapbox
        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${startPoint.longitude()},${startPoint.latitude()};${stopoverPoint.longitude()},${stopoverPoint.latitude()};${endPoint.longitude()},${endPoint.latitude()}?geometries=polyline6&steps=true&overview=full&access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}")

        // Lanza una coroutine para realizar la solicitud en segundo plano
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Procesa la respuesta JSON
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")

                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val geometry = route.getString("geometry") // Obtiene la cadena en formato polyline6
                    val decodedPoints = decodePolyline(geometry) // Decodifica la polyline6 en coordenadas

                    // Divide los puntos en tramos: inicio -> descanso y descanso -> final
                    val tramo1 = decodedPoints.takeWhile { it.latitude() <= stopoverPoint.latitude() }
                    val tramo2 = decodedPoints.dropWhile { it.latitude() <= stopoverPoint.latitude() }

                    withContext(Dispatchers.Main) {

                        // Llama a la función addMarker para agregar marcadores en los puntos de inicio, descanso y final
                        addMarker(startPoint, "start-point-symbol", "start_icon", mapView)
                        addMarker(stopoverPoint, "stopover-point-symbol", "stopover_icon", mapView)
                        addMarker(endPoint, "end-point-symbol", "end_icon", mapView)

                        mapView.getMapboxMap().getStyle { style ->
                            // Agrega una capa roja para el tramo inicio -> descanso
                            if (tramo1.isNotEmpty()) {
                                val sourceInicioDescanso = geoJsonSource("route-source-inicio-descanso") {
                                    geometry(LineString.fromLngLats(tramo1))
                                }
                                style.addSource(sourceInicioDescanso)
                                val layerInicioDescanso = lineLayer("route-layer-inicio-descanso", "route-source-inicio-descanso") {
                                    lineColor("#FF0000") // Rojo
                                    lineWidth(5.0)
                                }
                                style.addLayer(layerInicioDescanso)
                            }

                            // Agrega una capa verde para el tramo descanso -> final
                            if (tramo2.isNotEmpty()) {
                                val sourceDescansoFinal = geoJsonSource("route-source-descanso-final") {
                                    geometry(LineString.fromLngLats(tramo2))
                                }
                                style.addSource(sourceDescansoFinal)
                                val layerDescansoFinal = lineLayer("route-layer-descanso-final", "route-source-descanso-final") {
                                    lineColor("#228B22") // Verde
                                    lineWidth(5.0)
                                }
                                style.addLayer(layerDescansoFinal)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("drawRouteCoordinates", "Error al obtener la ruta: ${e.message}")
            }
        }
    }

    // Función para agregar un marcador en un punto específico


    fun clearPreviousRoutes() {
        mapView.getMapboxMap().getStyle { style ->
            // Eliminar las fuentes de rutas anteriores
            routeSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            routeSources.clear() // Limpiamos la lista de fuentes

            // Eliminar las capas de rutas anteriores
            routeLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            routeLayers.clear() // Limpiamos la lista de capas

            // Eliminar las fuentes de pines anteriores
            pinSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            pinSources.clear() // Limpiamos la lista de fuentes de pines

            // Eliminar las capas de pines anteriores
            pinLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            pinLayers.clear() // Limpiamos la lista de capas de pines
        }
    }

}
