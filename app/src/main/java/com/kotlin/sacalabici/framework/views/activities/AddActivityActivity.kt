package com.kotlin.sacalabici.framework.views.activities

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.model.ActivityInfo
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Informacion
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.databinding.ActivityAddactivityBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.fragments.AddActivityInfoFragment
import com.kotlin.sacalabici.framework.views.fragments.AddActivityRouteFragment
import com.kotlin.sacalabici.framework.views.fragments.RutasFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddActivityActivity: AppCompatActivity(),
    AddActivityInfoFragment.OnFragmentInteractionListener,
        AddActivityRouteFragment.OnRutaConfirmListener
{
    private lateinit var binding: ActivityAddactivityBinding
    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var viewModelRoute: MapViewModel
    private lateinit var type: String
    private var isAddActivityRouteFragmentVisible = false

    private lateinit var rodadaInformation: Informacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addactivity)

        initializeBinding()
        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)
        viewModelRoute = ViewModelProvider(this).get(MapViewModel::class.java)

        // Guardar el tipo de actividad a crear
        type = intent.getStringExtra("type").toString()

        // Observa los cambios en los LiveData del ViewModel
        observeViewModel()

        if (savedInstanceState == null) {
            // Crea una instancia del fragmento
            val fragment = AddActivityInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
            // Agrega el fragmento al contenedor de la vista de la Activity
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentAddActivity, fragment)
                .commit()
        }
    }

    private fun initializeBinding(){
        binding = ActivityAddactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /*
    * Función llamada desde AddActivityInfoFragment
    * Recibe la información general del formulario en el primer fragmento
    * */
    override fun receiveInformation(
        title: String,
        date: String,
        hour: String,
        minutes: String,
        hourDur: String,
        minutesDur: String,
        ubi: String,
        description: String
    ) {
        // Almacenamiento de datos escritos
        val info = ActivityInfo(title, date, hour, minutes, hourDur, minutesDur, ubi, description)
        viewModel.activityInfo.value = info

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateAct: Date = dateFormat.parse(date) ?: throw IllegalArgumentException("Fecha inválida")

        val hourAct = "$hour:$minutes"
        val duration = "$hourDur:$minutesDur"

        if (type == "Rodada") {
            val rodadaInfo = Informacion(title, dateAct, hourAct, ubi, description, duration, "", "Rodada")
            rodadaInformation = rodadaInfo

        } else if (type == "Taller") {
            val tallerInfo = Informacion(title, dateAct, hourAct, ubi, description, duration, "", "Taller")
            val taller = ActivityModel(listOf(tallerInfo))
            viewModel.postActivityTaller(taller)

        } else if (type == "Evento") {
            val eventoInfo = Informacion(title, dateAct, hourAct, ubi, description, duration, "", "Evento")
            val evento = ActivityModel(listOf(eventoInfo))
            viewModel.postActivityEvento(evento)
        }
    }

    /*
    * Función que llama la lista de rutas desde el viewModel
    * */
    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val addActivityRouteFragment = fragmentManager.findFragmentById(R.id.fragmentAddActivity)

        Log.d("ToggleAddActivityRoute", addActivityRouteFragment.toString())

        if (isAddActivityRouteFragmentVisible) {
            // Si el fragmento ya está visible, lo eliminamos
            if (addActivityRouteFragment != null) {
                fragmentManager.beginTransaction()
                    .remove(addActivityRouteFragment)
                    .addToBackStack(null)
                    .commit()
                Log.d(              "ToggleAddActivityRoute", "Fragmento AddActivityRouteFragment eliminado")
            }
            isAddActivityRouteFragmentVisible = false
        } else {
            // Si el fragmento no está visible, lo añadimos
            viewModelRoute.getRouteList() // Esto activará la observación y añadirá el fragmento
            isAddActivityRouteFragmentVisible = true
            Log.d("ToggleAddActivityRoute", "Fragmento AddActivityRouteFragment añadido")
        }
    }

    /*
    * Función llamada desde AddActivityInfoFragment
    * Termina la actividad
    * */
    override fun onCloseClicked() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    /*
    * Función llamada desde AddActivityInfoFragment
    * Si es rodada, cambia al siguiente fragmento para elegir una ruta
    * */
    override fun onNextClicked(type: String) {
        Log.d("Tipo en onNextClicked: ", type)
        if (type == "Rodada") {
            toggleRutasList()

        } else {
            Toast.makeText(this, "Actividad registrada", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    /*
    * Función que observa los cambios en los LiveData del ViewModel
    * */
    private fun observeViewModel() {
        // Observa los LiveData del ViewModel
        viewModelRoute.routeObjectLiveData.observe(this, Observer { rutasList ->
            rutasList?.let {
                // Si la lista de rutas se ha obtenido, crea el fragmento RutasFragment
                val routeFragment = AddActivityRouteFragment.newInstance(it, viewModelRoute.lastSelectedRuta)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentAddActivity, routeFragment)
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                showToast("Error al obtener la lista de rutas.")
            }
        })

        viewModelRoute.toastMessageLiveData.observe(this, Observer { message ->
            showToast(message)
        })
    }

    /*
    * Función llamada desde AddActivityRouteFragment
    * Recibe la ruta seleccionada por el usuario
    * */
    override fun onRutaConfirmed(rutaID: String) {
        val rodada = Rodada(listOf(rodadaInformation), rutaID)
        viewModel.postActivityRodada(rodada)
        showToast("Actividad registrada")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}