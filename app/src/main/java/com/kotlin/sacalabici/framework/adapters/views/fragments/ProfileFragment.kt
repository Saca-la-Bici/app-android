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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ProfileViewModel
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
        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getProfile("VF7Xd7Bgcdd54dUFzpPajm0QwtI2")
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfile("VF7Xd7Bgcdd54dUFzpPajm0QwtI2").observe(viewLifecycleOwner) { profile ->
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
            if (profile != null) {
                profile.printProfileDetails()
            }
        }
    }

    private fun initializeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
    }

    private fun setupEditButton() {
        binding.btnEditProfile.setOnClickListener {
            passToEditActivity(requireContext())
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
