package com.kotlin.sacalabici.framework.views.activities.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.model.ActivityData
import com.kotlin.sacalabici.data.network.model.ActivityInfo
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.databinding.ActivityAddactivityBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.fragments.ModifyActivityInfoFragment
import com.kotlin.sacalabici.framework.views.fragments.ModifyActivityRouteFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModifyActivityActivity: AppCompatActivity(),

    ModifyActivityInfoFragment.OnNextInteractionListener,
    ModifyActivityRouteFragment.OnRutaConfirmListener
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
    private lateinit var typeAct: String
    private var peopleEnrolled: Int = 0
    private var state: Boolean = true
    private var foro: String? = null
    private var register: ArrayList<String>? = null
    private var idRoute: String? = null

    private var selectedImageUri: Uri? = null
    private var originalImageUrl: String? = null

    private lateinit var rodadaInformation: ActivityData

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
        originalImageUrl = intent.getStringExtra("url")
        typeAct = intent.getStringExtra("typeAct").toString()
        peopleEnrolled = intent.getIntExtra("peopleEnrolled", 0)
        state = intent.getBooleanExtra("state", true)
        foro = intent.getStringExtra("foro")
        register = intent.getStringArrayListExtra("register")
        idRoute = intent.getStringExtra("idRoute")

        Log.d("ModifyActivity onCreate", "typeAct: $typeAct")
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
                    putString("url", originalImageUrl)
                    putString("typeAct", typeAct)
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
    * Función llamada desde ModifyActivityInfoFragment
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
        imageUri: Uri?
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

        if (typeAct == "Rodada") {
            val rodadaInfo = ActivityData(id, title, formattedDate, hourAct, ubi, description,
                duration, imageUri, "Rodada", peopleEnrolled, state, foro, register, idRoute)
            rodadaInformation = rodadaInfo

        } else if (typeAct == "Taller") {
            val taller = ActivityData(id, title, formattedDate, hourAct, ubi, description,
                duration, imageUri, "Taller", peopleEnrolled, state, foro, register, idRoute)
            viewModel.patchActivityTaller(taller, this@ModifyActivityActivity) { result ->
                result.fold(
                    onSuccess = {
                        showToast("Taller modificado exitosamente")
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                    onFailure = { error ->
                        showToast("Error al modificar el taller")
                    }
                )
            }

        } else if (typeAct == "Evento") {
            val evento = ActivityData(id, title, formattedDate, hourAct, ubi, description,
                duration, imageUri, "Evento", peopleEnrolled, state, foro, register, idRoute)
            Log.d("ModifyActivity", "Entra a if de evento")
            viewModel.patchActivityEvento(evento, this@ModifyActivityActivity) { result ->
                result.fold(
                    onSuccess = {
                        Log.d("ModifyActivity", "Entra a onSuccess")
                        showToast("Evento modificado exitosamente")
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                    onFailure = { error ->
                        showToast("Error al modificar el evento")
                    }
                )
            }
        }
    }

    /*
    * Función que llama la lista de rutas desde el viewModel
    * */
    private fun toggleRutasList() {
        val fragmentManager = supportFragmentManager
        val modifyActivityFragment = fragmentManager.findFragmentById(R.id.fragmentAddActivity)

        if (isAddActivityRouteFragmentVisible) {
            // Si el fragmento ya está visible, lo eliminamos
            if (modifyActivityFragment != null) {
                fragmentManager.beginTransaction()
                    .remove(modifyActivityFragment)
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
        }
    }

    /*
    * Función que observa los cambios en los LiveData del ViewModel
    * */
    private fun observeViewModel() {
        // Observa los LiveData del ViewModel
        viewModelRoute.routeObjectLiveData.observe(this, Observer { rutasList ->
            rutasList?.let {
                // Si la lista de rutas se ha obtenido, crea el fragmento RutasFragment con el ID de la ruta
                val routeFragment = ModifyActivityRouteFragment.newInstance(it, idRoute)
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
        rodadaInformation.idRouteBase = rutaID
        viewModel.patchActivityRodada(rodadaInformation, this@ModifyActivityActivity) { result ->
            result.fold(
                onSuccess = {
                    showToast("Rodada modificada exitosamente")
                    setResult(Activity.RESULT_OK)
                    finish()
                },
                onFailure = { error ->
                    showToast("Error al modificar la rodada")
                }
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}