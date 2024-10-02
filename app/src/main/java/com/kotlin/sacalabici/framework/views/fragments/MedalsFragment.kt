package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentMedalsBinding
import com.kotlin.sacalabici.framework.views.fragments.TotalMedalsFragment

class MedalsFragment : Fragment() {
    private var _binding: FragmentMedalsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMedalsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupNextMedalsButton()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Función para que el botón de Regresar de lleve a SettingsFragment
    private fun setupNextMedalsButton() {
        val btnMedals = binding.BNext
        btnMedals.setOnClickListener {
            // Navegar a SettingFragment y reemplazar el contenido en el contenedor principal de MainActivity
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, TotalMedalsFragment())
                .commit()
        }
    }
}
