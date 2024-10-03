package com.kotlin.sacalabici.framework.views.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.sacalabici.databinding.FragmentActivitiesBinding
import com.kotlin.sacalabici.framework.views.activities.AddActivityActivity
import com.kotlin.sacalabici.framework.adapters.ActivitiesPagerAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class ActivitiesFragment: Fragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        binding.fabAddActivity.visibility = View.GONE
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()
        setupObservers()
        loadInitialData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeComponents() {
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()
        Log.d("ActivitiesFragment", "Stored permissions: $storedPermissions")
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
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Taller")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Evento")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
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