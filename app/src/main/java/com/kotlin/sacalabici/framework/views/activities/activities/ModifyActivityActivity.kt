package com.kotlin.sacalabici.framework.views.activities.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.kotlin.sacalabici.framework.views.fragments.ModifyActivityInfoFragment
import com.kotlin.sacalabici.framework.views.fragments.ModifyActivityRouteFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModifyActivityActivity: AppCompatActivity(),
    AddActivityInfoFragment.OnFragmentInteractionListener,
    AddActivityRouteFragment.OnRutaConfirmListener
{

    private lateinit var binding: ActivityAddactivityBinding
    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var viewModelRoute: MapViewModel
    private var isAddActivityRouteFragmentVisible = false

    // Variables del intent
    private lateinit var id: String
    private lateinit var title: String
    private lateinit var date: String
    private lateinit var hour: String
    private lateinit var ubi: String
    private lateinit var desc: String
    private lateinit var hourDur: String
    private var url: String? = null
    private lateinit var type: String

    private lateinit var rodadaInformation: Informacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addactivity)

        initializeBinding()
        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)
        viewModelRoute = ViewModelProvider(this).get(MapViewModel::class.java)

        // Guarda la información pasada en intent
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()
        date = intent.getStringExtra("date").toString()
        hour = intent.getStringExtra("hour").toString()
        ubi = intent.getStringExtra("ubi").toString()
        desc = intent.getStringExtra("desc").toString()
        hourDur = intent.getStringExtra("hourDur").toString()
        type = intent.getStringExtra("type").toString()

        // Observa los cambios en los LiveData del ViewModel
        observeViewModel()

        if (savedInstanceState == null) {
            // Crea una instancia del fragmento
            val fragment = ModifyActivityInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("date", date)
                    putString("hour", hour)
                    putString("ubi", ubi)
                    putString("desc", desc)
                    putString("hourDur", hourDur)
                    putString("url", url)
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
        description: String,
        image: Uri?
    ) {
        // Almacenamiento de datos escritos
        val info = ActivityInfo(title, date, hour, minutes, hourDur, minutesDur, ubi, description)
        viewModel.activityInfo.value = info

        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate: Date = inputDateFormat.parse(date) ?: throw IllegalArgumentException("Fecha inválida")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = outputDateFormat.format(parsedDate)

        val hourAct = "$hour:$minutes"
        val duration = "$hourDur horas $minutesDur minutos"



        if (type == "Rodada") {
            val rodadaInfo = Informacion(title, formattedDate, hourAct, ubi, description, duration, image, "Rodada")
            rodadaInformation = rodadaInfo

        } else if (type == "Taller") {
            val tallerInfo = Informacion(title, formattedDate, hourAct, ubi, description, duration, image, "Taller")
            val taller = ActivityModel(listOf(tallerInfo))
            viewModel.postActivityTaller(taller, this)

        } else if (type == "Evento") {
            val eventoInfo = Informacion(title, formattedDate, hourAct, ubi, description, duration, image, "Evento")
            val evento = ActivityModel(listOf(eventoInfo))
            viewModel.postActivityEvento(evento, this)
        }
    }

    /*
    * Función que llama la lista de rutas desde el viewModel
    * */
    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val addActivityRouteFragment = fragmentManager.findFragmentById(R.id.fragmentAddActivity)

        if (isAddActivityRouteFragmentVisible) {
            // Si el fragmento ya está visible, lo eliminamos
            if (addActivityRouteFragment != null) {
                fragmentManager.beginTransaction()
                    .remove(addActivityRouteFragment)
                    .addToBackStack(null)
                    .commit()
            }
            isAddActivityRouteFragmentVisible = false
        } else {
            // Si el fragmento no está visible, lo añadimos
            viewModelRoute.getRouteList() // Esto activará la observación y añadirá el fragmento
            isAddActivityRouteFragmentVisible = true
        }
    }

    /*
    * Función llamada desde ModifyActivityInfoFragment
    * Termina la actividad
    *
    */
    override fun onCloseClicked() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    /*
    * Función llamada desde ModifyActivityInfoFragment
    * Si es rodada, cambia al siguiente fragmento para elegir una ruta
    * */
    override fun onNextClicked(type: String) {
        if (type == "Rodada") {
            toggleRutasList()

        } else {
            Toast.makeText(this, "Actividad modificada", Toast.LENGTH_SHORT).show()
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
                val routeFragment = ModifyActivityRouteFragment.newInstance(it, viewModelRoute.lastSelectedRuta)
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
    * Función llamada desde ModifyActivityRouteFragment
    * Recibe la ruta seleccionada por el usuario
    * */
    override fun onRutaConfirmed(rutaID: String) {
        val rodada = Rodada(listOf(rodadaInformation), rutaID)
        viewModel.postActivityRodada(rodada, this)
        showToast("Actividad registrada")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}