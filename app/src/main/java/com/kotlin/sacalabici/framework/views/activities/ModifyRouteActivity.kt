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

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.databinding.ActivityModificarrutaBinding
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.helpers.MapHelper
import com.kotlin.sacalabici.utils.InputValidator
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView

/**
 * Activity para registrar una nueva ruta, donde los usuarios pueden interactuar con un mapa para establecer
 * puntos de inicio, descanso y final, calcular la distancia, seleccionar un nivel de dificultad y proporcionar
 * detalles como el título y tiempo estimado de la ruta.
 */
class ModifyRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModificarrutaBinding
    private lateinit var viewModel: MapViewModel
    // Variables de UI para el mapa, campo de distancia y nivel de dificultad
    private lateinit var mapViewForm: MapView
    private lateinit var etDistancia: EditText
    private lateinit var tvNivel: TextView
    private lateinit var btnEliminarRuta: ImageButton
    private lateinit var btnEnviar: Button
    private lateinit var spinnerHoras: Spinner
    private lateinit var spinnerMinutos: Spinner

    // Puntos de la ruta (inicio, descanso, final)
    private var startPoint: Point? = null
    private var stopoverPoint: Point? = null
    private var endPoint: Point? = null
    private var referencePoint1: Point? = null
    private var referencePoint2: Point? = null

    // Variable para almacenar el nivel de dificultad seleccionado
    private var nivelSeleccionado: String? = null
    private val niveles = arrayOf("Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Nivel 5") // Opciones de nivel

    private lateinit var etTitulo: EditText
    private lateinit var etID: String
    /**
     * Se ejecuta al crear la actividad. Inicializa los elementos de UI, el mapa y la lógica de validación de inputs.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificarruta)
        initializeBinding()

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        // Inicialización de los componentes de UI
        mapViewForm = findViewById(R.id.mapView)
        etDistancia = findViewById(R.id.etDistancia)
        tvNivel = findViewById(R.id.tvNivel) // Inicializa el TextView del nivel
        btnEliminarRuta = findViewById(R.id.btnEliminarRuta) // Usa la variable de clase
        btnEnviar = findViewById(R.id.btnEnviar)
        val btnBack: ImageButton = findViewById(R.id.btnBack)

        // Inicializa la clase MapHelper para manejar el mapa y los puntos de ruta
        val mapHelper = MapHelper(this)

        // Inicialización de otros elementos de UI (título, tiempo y botón de enviar)
        etTitulo = findViewById(R.id.etTitulo)

        spinnerHoras = findViewById(R.id.spinnerHoras)
        spinnerMinutos = findViewById(R.id.spinnerMinutos)

        setupSpinners()

        val extras = intent.extras
        if (extras != null) {
            val id = extras.getString("ID") ?: ""
            val titulo = extras.getString("TITULO")
            val distancia = extras.getString("DISTANCIA")
            val nivel = extras.getString("NIVEL")

            // Rellena los campos con los datos recibidos
            etTitulo.setText(titulo)
            etDistancia.setText(distancia)
            tvNivel.text = nivel
            nivelSeleccionado = nivel
            etID = id

            val coordenadasJson = extras.getString("COORDENADAS")
            if (coordenadasJson != null) {
                val coordenadasType = object : TypeToken<ArrayList<CoordenatesBase>>() {}.type
                val coordenadas: ArrayList<CoordenatesBase> =
                    Gson().fromJson(coordenadasJson, coordenadasType)
                mapHelper.drawRouteWithCoordinates(mapViewForm, coordenadas)

                if (coordenadas.size >= 5) {
                    startPoint = Point.fromLngLat(coordenadas[0].longitud, coordenadas[0].latitud)
                    referencePoint1 = Point.fromLngLat(coordenadas[1].longitud, coordenadas[1].latitud)
                    stopoverPoint = Point.fromLngLat(coordenadas[2].longitud, coordenadas[2].latitud)
                    referencePoint2 = Point.fromLngLat(coordenadas[3].longitud, coordenadas[3].latitud)
                    endPoint = Point.fromLngLat(coordenadas[4].longitud, coordenadas[4].latitud)
                }
            }

            verifyInputs()
        }

        btnBack.setOnClickListener{
            finish()
        }


        // Lógica para el botón de eliminar la ruta del mapa
        btnEliminarRuta.setOnClickListener {

            startPoint = null
            stopoverPoint = null
            endPoint = null
            referencePoint1 = null
            referencePoint2 = null

            mapHelper.clearPreviousRoutes()
            etDistancia.text.clear()
            tvNivel.text = "" // Esto limpia el nivel seleccionado

            // Inicializa la clase MapHelper para manejar el mapa y los puntos de ruta
            mapHelper.initializeMap(
                mapViewForm, etDistancia,
                onStartPointSet = { point -> startPoint = point },  // Almacena el punto de inicio
                onStopoverPointSet = { point ->
                    stopoverPoint = point
                },  // Almacena el punto de descanso
                onEndPointSet = { point -> endPoint = point },
                onReferencePoint1Set = { point -> referencePoint1 = point },
                onReferencePoint2Set = { point ->
                    referencePoint2 = point
                }// Almacena el punto final
            )

            // Mostrar un mensaje confirmando que la ruta ha sido eliminada
            Toast.makeText(
                this,
                "Ruta eliminada. Por favor, selecciona una nueva ruta.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Configurar TextWatcher para los EditTexts
        etTitulo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verifyInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etDistancia.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verifyInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        spinnerHoras.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                verifyInputs() // Llama a la función para verificar entradas
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Puedes manejar acciones cuando no se selecciona nada, si es necesario
            }
        }

        spinnerMinutos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                verifyInputs() // Llama a la función para verificar entradas
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Puedes manejar acciones cuando no se selecciona nada, si es necesario
            }
        }
        // Al hacer clic en el TextView del nivel, se abre un diálogo para seleccionar el nivel
        tvNivel.setOnClickListener {
            showlevelDialogue()  // Abre el diálogo de selección de nivel
        }

        binding.btnEnviar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val distancia = etDistancia.text.toString()
            val selectedHours = spinnerHoras.selectedItem.toString()
            val selectedMinutes = spinnerMinutos.selectedItem.toString()
            val tiempo = selectedHours + " horas " + selectedMinutes + " minutos"
            val nivel = nivelSeleccionado.toString()
            val id = etID

            val coordenadas = arrayListOf<CoordenatesBase>(
                CoordenatesBase(startPoint!!.latitude(), startPoint!!.longitude(),"start"),      // Punto de inicio
                CoordenatesBase(referencePoint1!!.latitude(), referencePoint1!!.longitude(),"reference1"),  // Punto de referencia 1
                CoordenatesBase(stopoverPoint!!.latitude(), stopoverPoint!!.longitude(),"stopover"),  // Punto de parada
                CoordenatesBase(referencePoint2!!.latitude(), referencePoint2!!.longitude(),"reference2"),  // Punto de referencia 2
                CoordenatesBase(endPoint!!.latitude(), endPoint!!.longitude(),"end")          // Punto final
            )

            val route = Route(titulo,distancia,tiempo,nivel,coordenadas)
            viewModel.putRoute(id, route)
            setResult(Activity.RESULT_OK)
            Toast.makeText(
                this,
                "Se ha modificado la ruta con éxito",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        // Lógica del botón de enviar cuando se hace clic
    }

    private fun initializeBinding() {
        binding = ActivityModificarrutaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupHourSpinner(horas: String?) {
        // Genera una lista de 0 a 100
        val hours = (0..100).map { it.toString() }
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHoras.adapter = hourAdapter

        // Establece el valor inicial en el Spinner según el valor de horas
        horas?.let {
            val hourPosition = hours.indexOf(it)
            if (hourPosition >= 0) {
                spinnerHoras.setSelection(hourPosition)
            }
        }
    }

    private fun setupMinuteSpinner(minutos: String?) {
        // Genera una lista de 0 a 59
        val minutes = (0..59).map { it.toString() }
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMinutos.adapter = minuteAdapter

        // Establece el valor inicial en el Spinner según el valor de minutos
        minutos?.let {
            val minutePosition = minutes.indexOf(it)
            if (minutePosition >= 0) {
                spinnerMinutos.setSelection(minutePosition)
            }
        }
    }

    private fun setupSpinners() {
        val extras = intent.extras
        val horas = extras?.getString("HORAS").toString()
        val minutos = extras?.getString("MINUTOS").toString()

        // Configura los spinners con los valores iniciales
        setupHourSpinner(horas)
        setupMinuteSpinner(minutos)
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

        // Referencia a los Spinners de horas y minutos
        val spinnerHoras: Spinner = findViewById(R.id.spinnerHoras)
        val spinnerMinutos: Spinner = findViewById(R.id.spinnerMinutos)

        // Obtiene los valores seleccionados de los Spinners
        val horasSeleccionadas = spinnerHoras.selectedItem.toString().toInt()
        val minutosSeleccionados = spinnerMinutos.selectedItem.toString().toInt()

        // Referencia al botón de enviar
        val btnEnviar: Button = findViewById(R.id.btnEnviar)

        // Verificaciones de los campos
        val todosCamposLlenos = titulo.isNotEmpty() && distancia.isNotEmpty() // Verifica si todos los campos están llenos
        val nivelSeleccionado = nivelSeleccionado != null // Verifica si se ha seleccionado un nivel
        val rutaCompleta = startPoint != null && stopoverPoint != null && endPoint != null // Verifica si la ruta está completa (todos los puntos establecidos)

        // Verifica si ambos tiempos son cero
        val tiemposSonCero = horasSeleccionadas == 0 && minutosSeleccionados == 0

        // Si todos los campos están llenos, el nivel está seleccionado, la ruta está completa, y los tiempos no son cero
        if (todosCamposLlenos && nivelSeleccionado && rutaCompleta && !tiemposSonCero) {
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

        // Variable temporal para almacenar la selección del nivel
        var nivelSeleccionadoTemporal = -1 // Valor inicial de -1 indica que ningún nivel ha sido seleccionado

        // Crea un diálogo de selección con la lista de niveles
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona el nivel") // Título del diálogo
        builder.setSingleChoiceItems(niveles, -1) { _, which ->
            nivelSeleccionadoTemporal = which // Guarda el índice del nivel seleccionado temporalmente
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