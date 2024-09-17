package com.kotlin.sacalabici.framework.adapters.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.helpers.MapHelper
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

        var mapHelper = MapHelper()
        mapHelper.initializeMap(mapViewForm, etDistancia)

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etTiempo = findViewById<EditText>(R.id.etTiempo)
        val btnEnviar: Button = findViewById(R.id.btnEnviar)
        val btnSiguiente: Button = findViewById(R.id.btnSiguiente)

        btnSiguiente.isEnabled = false
        btnEnviar.isEnabled = false

        etTitulo.addTextChangedListener(textWatcher)
        etDistancia.addTextChangedListener(textWatcher)
        etTiempo.addTextChangedListener(textWatcher)

        tvNivel.setOnClickListener {
            showlevelDialogue() // Abre el diálogo para que el usuario seleccione un nivel
        }

        btnSiguiente.setOnClickListener {
            showAdditionalFields()
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

    private fun showAdditionalFields() {
        // Ajusta visibilidad
        val btnEnviar: Button = findViewById(R.id.btnEnviar)
        val etTitulo: EditText = findViewById(R.id.etTitulo)
        val etTiempo: EditText = findViewById(R.id.etTiempo)
        val etDistancia: EditText = findViewById(R.id.etDistancia)
        val btnSiguiente: Button = findViewById(R.id.btnSiguiente)
        val etInicio: EditText = findViewById(R.id.etInicio)
        val etDescanso: EditText = findViewById(R.id.etDescanso)
        val etFin: EditText = findViewById(R.id.etFin)
        tvNivel = findViewById(R.id.tvNivel) // Inicializamos el TextView del nivel

        // Oculta los campos actuales
        etTitulo.visibility = View.GONE
        etTiempo.visibility = View.GONE
        btnSiguiente.visibility = View.GONE
        tvNivel.visibility = View.GONE

        // Muestra los campos nuevos
        etDistancia.visibility = View.VISIBLE
        etInicio.visibility = View.VISIBLE
        etDescanso.visibility = View.VISIBLE
        etFin.visibility = View.VISIBLE
        btnEnviar.visibility = View.VISIBLE
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
        val inicio = findViewById<EditText>(R.id.etInicio).text.toString().trim()
        val descanso = findViewById<EditText>(R.id.etDescanso).text.toString().trim()
        val fin = findViewById<EditText>(R.id.etFin).text.toString().trim()
        val btnEnviar: Button = findViewById(R.id.btnEnviar)
        val btnSiguiente: Button = findViewById(R.id.btnSiguiente)


        val todosCamposLlenos = titulo.isNotEmpty() && distancia.isNotEmpty() && tiempo.isNotEmpty() && inicio.isNotEmpty() && descanso.isNotEmpty() && fin.isNotEmpty()
        val primerosCamposLlenos = titulo.isNotEmpty() && tiempo.isNotEmpty()
        val nivelSeleccionado = nivelSeleccionado != null
        val rutaCompleta = startPoint != null && stopoverPoint != null && endPoint != null

        Log.d("VerifyInputs", "Titulo: $titulo, Distancia: $distancia, Tiempo: $tiempo, Nivel Seleccionado: $nivelSeleccionado, Ruta Completa: $rutaCompleta")

        if(primerosCamposLlenos && nivelSeleccionado){
            btnSiguiente.isEnabled = true
            btnSiguiente.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_able)
        }
        else{
            btnSiguiente.isEnabled = false
            btnSiguiente.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_disabled)
        }

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
            val url = URL("http://10.0.2.2:7070/mapa/registrarRuta")
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

            // Log del JSON que se va a enviar
            Log.d("sendRoute", "JSON a enviar: $jsonInputString")

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