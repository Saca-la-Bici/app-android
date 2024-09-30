package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentRegisterFaqBinding

class RegisterFAQFragment : Fragment() {
    private var _binding: FragmentRegisterFaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflar el layout y obtener el binding
        _binding = FragmentRegisterFaqBinding.inflate(inflater, container, false)

        // Botón para abrir RegisterFAQFragment
        returnToFAQ()

        // Retorna la vista raíz del binding
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Función para regresar a FAQFragment
    private fun returnToFAQ() {
        val btnFAQs = binding.BCerrar
        btnFAQs.setOnClickListener {
            // Navegar a RegisterFAQsFragment y reemplazar el contenido en el contenedor principal de MainActivity
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, FAQFragment())
                .addToBackStack(null) // Para permitir navegar hacia atrás
                .commit()
        }
    }
}
