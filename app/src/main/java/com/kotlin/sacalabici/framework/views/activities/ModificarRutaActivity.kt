package com.kotlin.sacalabici.framework.views.activities

/**
 * File: ModificarRutaActivity.kt
 * Description:Esta clase permite a los usuarios registrar una nueva ruta interactuando con un mapa
 *  *              de Mapbox. Los usuarios pueden seleccionar puntos de inicio, descanso y final,
 *  *              calcular la distancia entre ellos, asignar un nivel de dificultad y proporcionar
 *  *              detalles como el título y tiempo estimado para la ruta. La información de la ruta
 *  *              se envía a un servicio remoto para guardarla.
 * Date: 18/09/2024
 * Changes:
 */

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.CoordenadasBase
import com.kotlin.sacalabici.framework.services.RutasService
import com.kotlin.sacalabici.helpers.MapHelper
import com.kotlin.sacalabici.utils.InputValidator
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import kotlinx.coroutines.launch

/**
 * Activity para registrar una nueva ruta, donde los usuarios pueden interactuar con un mapa para establecer
 * puntos de inicio, descanso y final, calcular la distancia, seleccionar un nivel de dificultad y proporcionar
 * detalles como el título y tiempo estimado de la ruta.
 */
class ModificarRutaActivity : AppCompatActivity() {
    // Variables de UI para el mapa, campo de distancia y nivel de dificultad
    private lateinit var mapViewForm: MapView
    private lateinit var etDistancia: EditText
    private lateinit var tvNivel: TextView
    private lateinit var btnEliminarRuta: ImageButton
    private lateinit var btnEnviar: Button

    // Puntos de la ruta (inicio, descanso, final)
    private var startPoint: Point? = null
    private var stopoverPoint: Point? = null
    private var endPoint: Point? = null

    // Variable para almacenar el nivel de dificultad seleccionado
    private var nivelSeleccionado: String? = null
    private val niveles = arrayOf("Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Nivel 5") // Opciones de nivel

    private lateinit var etTitulo: EditText
    private lateinit var etTiempo: EditText
    private lateinit var etID: String
    /**
     * Se ejecuta al crear la actividad. Inicializa los elementos de UI, el mapa y la lógica de validación de inputs.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificarruta)

        // Inicialización de los componentes de UI
        mapViewForm = findViewById(R.id.mapView)
        etDistancia = findViewById(R.id.etDistancia)
        tvNivel = findViewById(R.id.tvNivel) // Inicializa el TextView del nivel
        btnEliminarRuta = findViewById(R.id.btnEliminarRuta) // Usa la variable de clase
        btnEnviar = findViewById(R.id.btnEnviar)

        // Inicializa la clase MapHelper para manejar el mapa y los puntos de ruta
        var mapHelper = MapHelper(this)

        // Inicialización de otros elementos de UI (título, tiempo y botón de enviar)
        etTitulo = findViewById(R.id.etTitulo)
        etTiempo = findViewById(R.id.etTiempo)

        val extras = intent.extras
        if (extras != null) {
            val id = extras.getString("ID") ?: ""
            val titulo = extras.getString("TITULO")
            val distancia = extras.getString("DISTANCIA")
            val tiempo = extras.getString("TIEMPO")
            val nivel = extras.getString("NIVEL")

            // Rellena los campos con los datos recibidos
            etTitulo.setText(titulo)
            etDistancia.setText(distancia)
            etTiempo.setText(tiempo)
            tvNivel.text = nivel
            nivelSeleccionado = nivel
            etID = id

            val coordenadasJson = extras.getString("COORDENADAS")
            if (coordenadasJson != null) {
                val coordenadasType = object : TypeToken<ArrayList<CoordenadasBase>>() {}.type
                val coordenadas: ArrayList<CoordenadasBase> = Gson().fromJson(coordenadasJson, coordenadasType)
                mapHelper.drawRouteWithCoordinates(mapViewForm,coordenadas)

                if (coordenadas.size >= 3) {
                    startPoint = Point.fromLngLat(coordenadas[0].longitud, coordenadas[0].latitud)
                    stopoverPoint = Point.fromLngLat(coordenadas[1].longitud, coordenadas[1].latitud)
                    endPoint = Point.fromLngLat(coordenadas[2].longitud, coordenadas[2].latitud)
                }
            }

            verifyInputs()
        }


        // Lógica para el botón de eliminar la ruta del mapa
        btnEliminarRuta.setOnClickListener {

            mapHelper.clearPreviousRoutes()
            etTiempo.text.clear()
            etDistancia.text.clear()
            tvNivel.text = "" // Esto limpia el nivel seleccionado

            // Inicializa la clase MapHelper para manejar el mapa y los puntos de ruta
            mapHelper.initializeMap(
                mapViewForm, etDistancia,
                onStartPointSet = { point -> startPoint = point },  // Almacena el punto de inicio
                onStopoverPointSet = { point -> stopoverPoint = point },  // Almacena el punto de descanso
                onEndPointSet = { point -> endPoint = point }  // Almacena el punto final
            )

            // Mostrar un mensaje confirmando que la ruta ha sido eliminada
            Toast.makeText(this, "Ruta eliminada. Por favor, selecciona una nueva ruta.", Toast.LENGTH_SHORT).show()
        }

        // Crea un validador de entradas para activar el botón de enviar si los campos están completos
        val inputValidator = InputValidator { verifyInputs() }

        // Agrega listeners para validar los campos conforme el usuario ingresa datos
        etTitulo.addTextChangedListener(inputValidator)
        etDistancia.addTextChangedListener(inputValidator)
        etTiempo.addTextChangedListener(inputValidator)

        // Al hacer clic en el TextView del nivel, se abre un diálogo para seleccionar el nivel
        tvNivel.setOnClickListener {
            showlevelDialogue()  // Abre el diálogo de selección de nivel
        }

        verifyInputs()

        // Lógica del botón de enviar cuando se hace clic
        btnEnviar.setOnClickListener {
            // Obtiene los valores ingresados por el usuario
            val titulo = etTitulo.text.toString()
            val distancia = etDistancia.text.toString()
            val tiempo = etTiempo.text.toString()
            val nivel = nivelSeleccionado.toString()
            val id = etID

            if (startPoint != null && stopoverPoint != null && endPoint != null) {
                lifecycleScope.launch {
                    val routeService = RutasService
                    val result = routeService.modifyRoute(id,
                        titulo, distancia, tiempo, nivel,
                        startPoint!!, stopoverPoint!!, endPoint!!
                    )
                    if (result) {
                        Toast.makeText(this@ModificarRutaActivity, "Ruta modificada exitosamente.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ModificarRutaActivity, "Error al modificar la ruta.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, establezca todos los puntos de la ruta.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStartPoint(point: Point) {
        startPoint = point
    }

    private fun updateStopoverPoint(point: Point) {
        stopoverPoint = point
    }

    private fun updateEndPoint(point: Point) {
        endPoint = point
    }


    /**
     * Verifica si los campos de entrada (título, distancia, tiempo) están completos, si se ha seleccionado
     * un nivel, y si la ruta (puntos de inicio, descanso y final) está completa. Habilita o deshabilita el
     * botón de enviar en función de estas verificaciones.
     */
    fun verifyInputs() {
        // Obtiene los valores de los campos de texto, eliminando espacios al inicio y final
        val titulo = findViewById<EditText>(R.id.etTitulo).text.toString().trim()
        val distancia = etDistancia.text.toString().trim()
        val tiempo = findViewById<EditText>(R.id.etTiempo).text.toString().trim()

        // Referencia al botón de enviar
        val btnEnviar: Button = findViewById(R.id.btnEnviar)

        // Verificaciones de los campos
        val todosCamposLlenos = titulo.isNotEmpty() && distancia.isNotEmpty() && tiempo.isNotEmpty() // Verifica si todos los campos están llenos
        val nivelSeleccionado = nivelSeleccionado != null // Verifica si se ha seleccionado un nivel
        val rutaCompleta = startPoint != null && stopoverPoint != null && endPoint != null // Verifica si la ruta está completa (todos los puntos establecidos)

        // Registra el estado de los puntos de la ruta para debugging
        Log.d("VerifyRuta", "Start: $startPoint, Stopover: $stopoverPoint, End: $endPoint")

        // Registra los datos ingresados por el usuario y el estado de las verificaciones para debugging
        Log.d("VerifyInputs", "Titulo: $titulo, Distancia: $distancia, Tiempo: $tiempo, Nivel Seleccionado: $nivelSeleccionado, Ruta Completa: $rutaCompleta")

        // Si todos los campos están llenos, el nivel está seleccionado y la ruta está completa, habilita el botón de enviar
        if (todosCamposLlenos && nivelSeleccionado && rutaCompleta) {
            btnEnviar.isEnabled = true
            btnEnviar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_able) // Cambia el color del botón a habilitado
        } else {
            // Si alguna condición no se cumple, deshabilita el botón de enviar
            btnEnviar.isEnabled = false
            btnEnviar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_disabled) // Cambia el color del botón a deshabilitado
        }
    }


    /**
     * Muestra un diálogo donde el usuario puede seleccionar un nivel de dificultad.
     * El nivel seleccionado se guarda en una variable y se actualiza el TextView correspondiente.
     */
    private fun showlevelDialogue() {
        // Registra la lista de niveles para debugging
        Log.d("Niveles", niveles.joinToString())

        // Variable temporal para almacenar la selección del nivel
        var nivelSeleccionadoTemporal = -1 // Valor inicial de -1 indica que ningún nivel ha sido seleccionado

        // Crea un diálogo de selección con la lista de niveles
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el nivel") // Título del diálogo
        builder.setSingleChoiceItems(niveles, -1) { _, which ->
            nivelSeleccionadoTemporal = which // Guarda el índice del nivel seleccionado temporalmente
            Log.d("NivelSeleccionado", nivelSeleccionadoTemporal.toString()) // Registra el índice seleccionado para debugging
        }

        // Botón de confirmación "Listo"
        builder.setPositiveButton("Listo") { dialog, _ ->
            if (nivelSeleccionadoTemporal in niveles.indices) {
                // Si el nivel seleccionado es válido, se guarda como nivel final y se actualiza el TextView
                nivelSeleccionado = niveles[nivelSeleccionadoTemporal] // Asigna el nivel seleccionado
                Log.d("Nivel", nivelSeleccionado.toString()) // Registra el nivel seleccionado
                tvNivel.text = nivelSeleccionado // Actualiza el TextView con el nivel seleccionado
            } else {
                // Si no se seleccionó un nivel válido, muestra un mensaje al usuario
                Toast.makeText(this, "Por favor, seleccione un nivel válido.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss() // Cierra el diálogo
        }

        // Botón para cancelar la selección
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo sin guardar ningún cambio
        }

        // Muestra el diálogo
        builder.create().show()
    }
}