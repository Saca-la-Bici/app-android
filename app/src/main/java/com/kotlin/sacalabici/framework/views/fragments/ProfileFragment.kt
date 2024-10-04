package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileBinding
import com.kotlin.sacalabici.framework.adapters.ProfileAdapter
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                // Cargar imagen de perfil usando Glide
                val profileImageUrl = it.pImage

                // Si la URL de la imagen no es nula ni vacía, cargarla con Glide
                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide
                        .with(requireContext())
                        .load(profileImageUrl) // Cargar la imagen desde la URL
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cachear la imagen
                        .placeholder(R.drawable.baseline_person_24) // Imagen por defecto mientras se carga
                        .error(R.drawable.baseline_person_24) // Imagen por defecto en caso de error
                        .into(binding.profileImage) // Colocar la imagen en el ImageView
                } else {
                    // Si la URL es nula o vacía, usar la imagen por defecto
                    binding.profileImage.setImageResource(R.drawable.baseline_person_24)
                }
                Log.d("imagen", profileImageUrl)
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
            val settingsFragment = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, settingsFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
