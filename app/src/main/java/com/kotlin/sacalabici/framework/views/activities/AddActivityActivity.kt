package com.kotlin.sacalabici.framework.views.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Informacion
import com.kotlin.sacalabici.databinding.ActivityAddactivityBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.fragments.AddActivityInfoFragment
import com.kotlin.sacalabici.framework.views.fragments.AddActivityRouteFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddActivityActivity: AppCompatActivity(),
    AddActivityInfoFragment.OnFragmentInteractionListener
{
    private lateinit var binding: ActivityAddactivityBinding
    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addactivity)

        initializeBinding()
        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)

        // Guardar el tipo de actividad a crear
        type = intent.getStringExtra("type").toString()

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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateAct: Date = dateFormat.parse(date) ?: throw IllegalArgumentException("Fecha inválida")

        val hourAct = "$hour:$minutes"
        val duration = "$hourDur:$minutesDur"

        if (type == "Rodada") {
            val rodadaInfo = Informacion(title, dateAct, hourAct, ubi, description, duration, "", "Rodada")
            viewModel.receiveRodadaInfo(rodadaInfo)

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
    * Función llamada desde AddActivityInfoFragment
    * Si es rodada, cambia al siguiente fragmento para elegir una ruta
    * */
    override fun onNextClicked(type: String) {
        if (type === "Rodada") {
            val routeFragment = AddActivityRouteFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentAddActivity, routeFragment)
                .addToBackStack(null) // Permite volver al fragmento anterior si presionas "atrás"
                .commit()
        } else {
            Toast.makeText(this, "Actividad completada", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    /*
    * Función llamada desde AddActivityRouteFragment
    * Recibe la ruta seleccionada por el usuario
    * */
    override fun receiveRuta() {

    }
}