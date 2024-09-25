package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityAddactivityBinding
import com.kotlin.sacalabici.framework.adapters.views.fragments.AddActivityInfoFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.AddActivityRouteFragment

class AddActivityActivity: AppCompatActivity(),
    AddActivityInfoFragment.OnFragmentInteractionListener
{
    private lateinit var binding: ActivityAddactivityBinding
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addactivity)

        initializeBinding()

        // Guardar el tipo de actividad a crear
        type = intent.getStringExtra("type").toString()

        // Verifica si el fragmento ya está agregado
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

        /*
        * Dependiendo si es Rodada, Taller o Evento será la información que espere
        * */

    }

    private fun initializeBinding(){
        binding = ActivityAddactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /*
    * Función que se llama desde el AddActivityInfoFragment
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
        }
    }
}