package com.kotlin.sacalabici.framework.adapters.views.fragments

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
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileBinding
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import com.kotlin.sacalabici.framework.adapters.views.activities.ProfileEditActivity

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

        setupEditButton()
        setupSettingsButton()

        editProfileLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK){
                viewModel.getProfile()
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.username.text = profile.user
            }
            if (profile != null) {
                binding.profileName.text = profile.name
            }
            if (profile != null) {
                binding.profileBlood.text = profile.bloodtype
            }
            if (profile != null) {
                binding.textRodadas.text = profile.activitiesCompleted.toString()
            }
            if (profile != null) {
                binding.textKilometros.text = "${profile.KmCompleted}km"
            }
        }
    }

    private fun initializeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
    }

    private fun setupEditButton() {
        val btnEditProfile = binding.btnEditProfile
        btnEditProfile.setOnClickListener {
            val intent = Intent(activity, ProfileEditActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
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

    private fun passToEditActivity(context: Context) {
        val intent = Intent(context, ProfileEditActivity::class.java)
        editProfileLauncher.launch(intent)
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