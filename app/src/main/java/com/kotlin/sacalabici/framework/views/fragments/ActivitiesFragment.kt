package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.sacalabici.databinding.FragmentActivitiesBinding
import com.kotlin.sacalabici.framework.views.activities.activities.AddActivityActivity
import com.kotlin.sacalabici.framework.adapters.ActivitiesPagerAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class ActivitiesFragment: Fragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    private lateinit var addActivityLauncher: ActivityResultLauncher<Intent>

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false) // Inflar el diseño del fragmento
        binding.fabAddActivity.visibility = View.GONE // Ocultar botón agregar actividad

        // Manejar los resultados de AddActivityActivity
        addActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Actualizar todas las actividades (eventos, rodadas, talleres)
                activitiesViewModel.getEventos()
                activitiesViewModel.getRodadas()
                activitiesViewModel.getTalleres()
            }
        }

        return binding.root
    }
    
    // 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()
        setupObservers()
        loadInitialData()
    }

    // Limpiar binding para optimizar uso de recursos
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeComponents() {
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()
        // Verificación de permiso para agregar actividad
        if (storedPermissions?.contains("Registrar actividad") == true) {
            binding.fabAddActivity.visibility = View.VISIBLE
            // Listener para agregar actividad
            addActivity()
        } else {
            binding.fabAddActivity.visibility = View.GONE
        }

        val adapter = ActivitiesPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Configura el TabLayout con el ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Rodadas"
                1 -> "Eventos"
                2 -> "Talleres"
                else -> null
            }
        }.attach()
    }

    private fun addActivity() {
        val btnRegister = binding.fabAddActivity
        btnRegister.setOnClickListener {
            showActivityDialogue()
        }
    }

    /*
    * Muestra una ventana para que el usuario elija el tipo de actividad
    * a crear. Puede ser una rodada, un taller o un evento.
    * */
    private fun showActivityDialogue() {
        val options = arrayOf("Añadir rodada", "Añadir taller", "Añadir evento")

        // Crear diálogo
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elige un tipo de actividad")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Rodada")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addActivityLauncher.launch(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Taller")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addActivityLauncher.launch(intent)
                }
                2 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Evento")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addActivityLauncher.launch(intent)
                }
            }
        }

        builder.show()
    }

    private fun setupObservers() {
        activitiesViewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            savePermissionsToSharedPreferences(permissions)
        }
    }

    private fun loadInitialData() {
        activitiesViewModel.getPermissions()
    }

    private fun savePermissionsToSharedPreferences(permissions: List<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet("permissions", permissions.toSet())
        editor.apply()
    }
}