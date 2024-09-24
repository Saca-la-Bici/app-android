package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentSettingsAdminBinding


class SettingsAdminFragment : Fragment() {
    private var _binding: FragmentSettingsAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsAdminBinding.inflate(inflater, container, false)

        binding.btnRoles.setOnClickListener {
            val modifyRolesFragment = ModifyRolesFragment()
            // Reemplazar el fragmento actual por SettingsFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, modifyRolesFragment) // Asegúrate de que este ID coincida con el contenedor de fragmentos en tu layout
                .addToBackStack(null) // Para permitir volver al fragmento anterior
                .commit()
        }

        binding.BBack.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Liberar el binding
    }
}
