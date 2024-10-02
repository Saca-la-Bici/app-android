package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileBinding
import com.kotlin.sacalabici.framework.adapters.views.fragments.EventFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.GlobalFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.MedalsFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsAdminFragment
import com.kotlin.sacalabici.framework.adapters.ProfileAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.ProfileEditFragment
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java) // Inicializa ViewModel

        // Configura el adaptador para ViewPager2
        val pagerAdapter = ProfileAdapter(this)
        binding.vFragment.adapter = pagerAdapter

        // Configura el TabLayout con ViewPager2
        TabLayoutMediator(binding.tabProfile, binding.vFragment) { tab, position ->
            tab.text = when (position) {
                0 -> "____________"
                1 -> "____________"
                2 -> "____________"
                else -> null
            }
        }.attach()

        // Configura el botón de edición
        setupEditButton()
        setupSettingsButton()

        // Inicializa el launcher para recibir el resultado de la edición de perfil
        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Actualiza los datos del perfil al regresar de ProfileEditActivity
                viewModel.getProfile()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observa los cambios en el perfil y actualiza la interfaz
        viewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.username.text = it.user
                binding.profileName.text = it.name
                binding.profileBlood.text = it.bloodtype
                binding.textRodadas.text = it.activitiesCompleted.toString()
                binding.textKilometros.text = "${it.KmCompleted}km"
            }
        }
    }

    // Configura el botón para editar el perfil
    private fun setupEditButton() {
        binding.btnEditProfile.setOnClickListener {
            val profileEditFragment = ProfileEditFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, profileEditFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSettingsButton() {
        val btnSettings = binding.btnSettings
        btnSettings.setOnClickListener {
            val settingsAdminFragment = SettingsAdminFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, settingsAdminFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
