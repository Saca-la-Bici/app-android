package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileBinding
import com.kotlin.sacalabici.framework.adapters.views.activities.ProfileEditActivity

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initializeFragment(EventFragment())

        val btnEventos = binding.btnEventos
        val btnAsistencia = binding.btnAsistencia
        val btnGlobal = binding.btnGlobal

        btnEventos.setOnClickListener {
            highlightCurrentFragment("Eventos", btnEventos, btnAsistencia, btnGlobal)
        }
        btnAsistencia.setOnClickListener {
            highlightCurrentFragment("Asistencia", btnEventos, btnAsistencia, btnGlobal)
        }
        btnGlobal.setOnClickListener {
            highlightCurrentFragment("Global", btnEventos, btnAsistencia, btnGlobal)
        }

        setupConfiguracionButton()
        setupEditarButton()

        return root
    }

    private fun initializeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
    }

    private fun setupConfiguracionButton() {
        val btnConfiguration = binding.btnConfiguration
        btnConfiguration.setOnClickListener {
            val settingsFragment = SettingsFragment()
            // Reemplazar el fragmento actual por SettingsFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, settingsFragment) // Asegúrate de que este ID coincida con el contenedor de fragmentos en tu layout
                .addToBackStack(null) // Para permitir volver al fragmento anterior
                .commit()
        }
    }


    private fun setupEditarButton() {
        val btnEditProfile = binding.btnEditProfile
        btnEditProfile.setOnClickListener {
            val intent = Intent(activity, ProfileEditActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun highlightCurrentFragment(
        currentFragment: String,
        btnEventos: ImageButton,
        btnAsistencia: ImageButton,
        btnGlobal: ImageButton
    ) {
        resetButtonStyles(btnEventos, btnAsistencia, btnGlobal)

        when (currentFragment) {
            "Eventos" -> {
                btnEventos.setImageResource(R.drawable.ic_event_selected)
                replaceFragment(EventFragment())
            }
            "Asistencia" -> {
                btnAsistencia.setImageResource(R.drawable.ic_check_selected)
                replaceFragment(MedalsFragment())
            }
            "Global" -> {
                btnGlobal.setImageResource(R.drawable.ic_global_selected)
                replaceFragment(GlobalFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
    }

    private fun resetButtonStyles(
        btnEventos: ImageButton,
        btnAsistencia: ImageButton,
        btnGlobal: ImageButton
    ) {
        btnEventos.setImageResource(R.drawable.ic_event)
        btnAsistencia.setImageResource(R.drawable.ic_check)
        btnGlobal.setImageResource(R.drawable.ic_global)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
