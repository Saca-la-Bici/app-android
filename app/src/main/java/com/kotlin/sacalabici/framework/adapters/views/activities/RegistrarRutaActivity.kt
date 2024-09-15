package com.kotlin.sacalabici.framework.adapters.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RegistrarRutaActivity: AppCompatActivity() {
    private lateinit var mapViewForm: MapView
    private lateinit var etDistancia: EditText
    private lateinit var tvNivel: TextView
    private var startPoint: Point? = null
    private var stopoverPoint: Point? = null
    private var stopoverPoint2: Point? = null
    private var stopoverPoint3: Point? = null
    private var endPoint: Point? = null

    private var nivelSeleccionado: String? = null // Variable para almacenar el nivel seleccionado
    private var nivelSeleccionadoTemporal: Int = -1
    private val niveles = arrayOf("Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Nivel 5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregarruta)

        mapViewForm = findViewById(R.id.mapView)
        etDistancia = findViewById(R.id.etDistancia)
        tvNivel = findViewById(R.id.tvNivel) // Inicializamos el TextView del nivel
        initializeMap()
        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etTiempo = findViewById<EditText>(R.id.etTiempo)
        val btnEnviar: Button = findViewById(R.id.btnEnviar)

        btnEnviar.isEnabled = false

        etTitulo.addTextChangedListener(textWatcher)
        etDistancia.addTextChangedListener(textWatcher)
        etTiempo.addTextChangedListener(textWatcher)

        tvNivel.setOnClickListener {
            showlevelDialogue() // Abre el diálogo para que el usuario seleccione un nivel
        }

        btnEnviar.setOnClickListener {
            val titulo = findViewById<EditText>(R.id.etTitulo).text.toString()
            val distancia = etDistancia.text.toString()
            val tiempo = findViewById<EditText>(R.id.etTiempo).text.toString()
            val nivel = nivelSeleccionado.toString()

            // Construcción del JSON
            val jsonObject = JSONObject().apply {
                put("titulo", titulo)
                put("distancia", distancia)
                put("tiempo", tiempo)
                put("nivel", nivel)
                put("startPoint", startPoint?.toString())  // Asegúrate de que `startPoint` pueda convertirse en String
                put("stopoverPoint", stopoverPoint?.toString())  // Asegúrate de que `stopoverPoint` pueda convertirse en String
                put("endPoint", endPoint?.toString())  // Asegúrate de que `endPoint` pueda convertirse en String
            }

            if (startPoint != null && stopoverPoint != null && endPoint != null) {
                lifecycleScope.launch {
                    val result = sendRoute(
                        titulo, distancia, tiempo, nivel, startPoint!!, stopoverPoint!!, endPoint!!
                    )
                    if (result) {
                        Toast.makeText(this@RegistrarRutaActivity, "Ruta guardada exitosamente.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegistrarRutaActivity, "Error al guardar la ruta.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, establezca todos los puntos de la ruta.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val textWatcher = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            verifyInputs()
        }

        override fun afterTextChanged(s: android.text.Editable?) {}
    }

    private fun verifyInputs() {
        val titulo = findViewById<EditText>(R.id.etTitulo).text.toString().trim()
        val distancia = etDistancia.text.toString().trim()
        val tiempo = findViewById<EditText>(R.id.etTiempo).text.toString().trim()
        val btnEnviar: Button = findViewById(R.id.btnEnviar)

        val todosCamposLlenos = titulo.isNotEmpty() && distancia.isNotEmpty() && tiempo.isNotEmpty()
        val nivelSeleccionado = nivelSeleccionado != null
        val rutaCompleta = startPoint != null && stopoverPoint != null && endPoint != null

        Log.d("VerifyInputs", "Titulo: $titulo, Distancia: $distancia, Tiempo: $tiempo, Nivel Seleccionado: $nivelSeleccionado, Ruta Completa: $rutaCompleta")

        if (todosCamposLlenos && nivelSeleccionado && rutaCompleta) {
            btnEnviar.isEnabled = true
            btnEnviar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_able)
        } else {
            btnEnviar.isEnabled = false
            btnEnviar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_disabled)
        }
    }


    // Función para mostrar el diálogo de selección de nivel
    private fun showlevelDialogue() {
        Log.d("Niveles", niveles.joinToString())
        var nivelSeleccionadoTemporal = -1 // Variable temporal para seleccionar el nivel

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el nivel")
        builder.setSingleChoiceItems(niveles, -1) { _, which ->
            nivelSeleccionadoTemporal = which // Guardamos el índice del nivel seleccionado
            Log.d("NivelSeleccionado", nivelSeleccionadoTemporal.toString())
        }

        builder.setPositiveButton("Listo") { dialog, _ ->
            if (nivelSeleccionadoTemporal in niveles.indices) {
                nivelSeleccionado = niveles[nivelSeleccionadoTemporal] // Asignamos el nivel final
                Log.d("Nivel", nivelSeleccionado.toString())
                tvNivel.text = nivelSeleccionado // Actualizamos el TextView con el nivel seleccionado
            } else {
                Toast.makeText(this, "Por favor, seleccione un nivel válido.", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show() // Mostramos el diálogo
    }



    private fun initializeMap() {
        mapViewForm.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11") {
            // Define the coordinates for Querétaro
            val queretaroCoordinates = Point.fromLngLat(-100.3899, 20.5888)

            // Move the camera to Querétaro
            mapViewForm.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(12.0) // Ajusta el zoom según sea necesario
                    .build()
            )
            enableLocationComponent()
            setupMapLongClickListener()
        }
    }

    private fun enableLocationComponent() {
        with(mapViewForm) {
            location.enabled = true
            location.puckBearing = PuckBearing.COURSE
        }
    }

    private fun setupMapLongClickListener() {
        mapViewForm.getMapboxMap().addOnMapLongClickListener(OnMapLongClickListener { point ->
            when {
                startPoint == null -> {
                    startPoint = point
                    Toast.makeText(this, "Punto de inicio establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "start-point-symbol", "start_icon")
                }
                stopoverPoint == null -> {
                    stopoverPoint = point
                    Toast.makeText(this, "Punto de descanso establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "stopover-point-symbol", "stopover_icon")
                }
                endPoint == null -> {
                    endPoint = point
                    Toast.makeText(this, "Punto final establecido.", Toast.LENGTH_SHORT).show()
                    addMarker(point, "end-point-symbol", "end_icon")
                    drawRoute()  // Aquí dibujas la ruta
                }
                else -> {
                    Toast.makeText(this, "Ya se han establecido todos los puntos.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        })
    }

    private fun addMarker(point: Point, symbolId: String, iconName: String) {
        mapViewForm.getMapboxMap().getStyle { style ->

            // Verifica si la imagen ya ha sido añadida al estilo, si no, la añade
            if (!style.styleLayerExists(iconName)) {
                val icon = BitmapFactory.decodeResource(resources, resources.getIdentifier(iconName, "drawable", packageName))
                style.addImage(iconName, icon)
            }

            // Crear la fuente GeoJSON
            val source = geoJsonSource(symbolId) {
                geometry(point)
            }
            style.addSource(source)

            // Ajuste del offset en función del icono específico
            val offset = when (iconName) {
                "start_icon" -> listOf(20.0, 0.0) // Desplazar hacia la derecha
                "end_icon" -> listOf(20.0, 0.0) // Desplazar hacia la derecha
                else -> listOf(0.0, -10.0) // Sin desplazamiento para otros íconos
            }

            // Crear la capa del símbolo con la imagen específica
            val symbolLayer = symbolLayer(symbolId + "-layer", symbolId) {
                iconImage(iconName)  // Aquí usas el ícono específico
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                iconSize(0.07)

                // Ajusta el ancla para que la parte inferior esté en el punto presionado
                iconAnchor(IconAnchor.BOTTOM)

                // Ajuste del iconOffset basado en el ícono
                iconOffset(offset)
            }
            style.addLayer(symbolLayer)
        }
    }



    private fun drawRoute() {
        val points = listOfNotNull(startPoint, stopoverPoint, stopoverPoint2, stopoverPoint3, endPoint)
        if (points.size < 2) {
            Toast.makeText(this, "Establezca al menos dos puntos para crear la ruta.", Toast.LENGTH_SHORT).show()
            return
        }

        val origin = points.first()
        val destination = points.last()
        val waypoints = points.subList(1, points.size - 1)

        val waypointsCoordinates = waypoints.joinToString(";") { "${it.longitude()},${it.latitude()}" }

        val url = URL("https://api.mapbox.com/directions/v5/mapbox/cycling/${origin.longitude()},${origin.latitude()};$waypointsCoordinates;${destination.longitude()},${destination.latitude()}?geometries=geojson&access_token=sk.eyJ1Ijoic2FtaXIyNzciLCJhIjoiY20wbWwxOXZ0MDNwNTJtb3J0cHN2Z3NmdCJ9.UafXR_Ln3yfnnVwqE2-_Dg")

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
                        mapViewForm.getMapboxMap().getStyle { style ->
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

    private suspend fun sendRoute(
        titulo: String,
        distancia: String,
        tiempo: String,
        nivel: String,
        start: Point,
        stopover: Point,
        end: Point
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://[SU_IP_AQUI]:7070/mapa/registrarRuta") // Asegúrate de usar la IP correcta
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val coordinatesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("latitud", start.latitude())
                    put("longitud", start.longitude())
                    put("tipo", "inicio")
                })
                put(JSONObject().apply {
                    put("latitud", stopover.latitude())
                    put("longitud", stopover.longitude())
                    put("tipo", "descanso")
                })
                put(JSONObject().apply {
                    put("latitud", end.latitude())
                    put("longitud", end.longitude())
                    put("tipo", "final")
                })
            }

            val jsonInputString = JSONObject().apply {
                put("titulo", titulo)
                put("distancia", distancia)
                put("tiempo", tiempo)
                put("nivel", nivel)
                put("coordenadas", coordinatesArray)
            }.toString()

            connection.outputStream.use {
                it.write(jsonInputString.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            connection.disconnect()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("sendRoute", "Respuesta del servidor: $responseMessage")
                true
            } else {
                Log.e("sendRoute", "Error en la solicitud: $responseCode - $responseMessage")
                false
            }
        } catch (e: Exception) {
            Log.e("sendRoute", "Excepción en la solicitud: ${e.message}")
            false
        }
    }


}