//package com.kotlin.sacalabici.framework.views.fragments
package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentProfileEditBinding
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.framework.adapters.views.fragments.EventFragment


class ProfileEditFragment: Fragment() {
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root: View = binding.root

        setupGenderDropdown()
        setupBloodDropdown()
        setupBackButton()

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
            println("cacaaaaaaaaaaaaaa")
            println(profile)
//            if (profile.user != null) {
//                binding.username.text = profile.user
//            }
//            if (profile != null) {
//                binding.name.text = profile.name
//            }
//            if (profile != null) {
//                binding.bloodDropDown.text = profile.bloodtype
//            }
//            if (profile != null) {
//                binding.genderDropDown.text = profile.activitiesCompleted.toString()
//            }
//            if (profile != null) {
//                binding.emergencyNumber.text = "${profile.KmCompleted}km"
//            }
        }
    }

    private fun initializeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
    }

    private fun setupGenderDropdown() {
        val genderDropdownConfig = binding.genderDropDown
        val genders = resources.getStringArray(R.array.genders)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, genders)
        genderDropdownConfig.setAdapter(arrayAdapter)

        val defaultValue = "Masculino"
        genderDropdownConfig.setText(defaultValue, false)

        val index = arrayAdapter.getPosition(defaultValue)
        if (index >= 0) {
            genderDropdownConfig.setSelection(index)
        }
    }

    private fun setupBloodDropdown() {
        val bloodDropdownConfig = binding.bloodDropDown
        val bloodTypes = resources.getStringArray(R.array.bloodTypes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, bloodTypes)
        bloodDropdownConfig.setAdapter(arrayAdapter)

        val defaultValue = "O+"
        bloodDropdownConfig.setText(defaultValue, false)

//        val index = arrayAdapter.getPosition(defaultValue)
//        if (index >= 0) {
////            bloodDropdownConfig.setSelection(index)
//        }
    }


    private fun setupBackButton() {
        val backButton = binding.btnBack
        backButton.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, profileFragment)
                .addToBackStack(null)
                .commit()
        }
    }


}

