package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.databinding.FragmentProfileEditBinding
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import java.io.File
import kotlinx.coroutines.*


class ProfileEditFragment: Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ProfileViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val root: View = binding.root

        setupGenderDropdown()
        setupBloodDropdown()
        setupBackButton()
        setupUploadButton()
        setUpEditImageButton()
        registerImagePicker()

        editProfileLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK){
                viewModel.getProfile()
            }
        }

        return root
    }

//    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.username.setText(profile.user)
                binding.name.setText(profile.name)
//                binding.genderDropDown.setText(profile.activitiesCompleted, false)
                binding.bloodDropDown.setText(profile.bloodtype, false)
                binding.emergencyNumber.setText(profile.emergencyNumber)
                val profileImageUrl = profile.pImage

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
            }
        }
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

//        val defaultValue = "A-"
//        bloodDropdownConfig.setText(defaultValue, false)

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

    private fun setupUploadButton() {
        val saveButton = binding.btnSave
        saveButton.setOnClickListener {
            val image = selectedImageUri
            val name = binding.name.text.toString()
            val username = binding.username.text.toString()
            val blood = binding.bloodDropDown.text.toString()
            val emergencyNum = binding.emergencyNumber.text.toString()
            val profile = Profile(username, name, blood, emergencyNum, 0, 0, 0.0, image)
            val context: Context = requireContext()

            lifecycleScope.launch {
                val success = viewModel.patchProfile(profile, context)
                if (success) {
                    val profileFragment = ProfileFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, profileFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpEditImageButton(){
        val imageButton = binding.profileImageLayout
        imageButton.setOnClickListener{
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageLauncher.launch(intent)
        }
    }

    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.profileImage.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanTemporaryFiles()
    }

    private fun cleanTemporaryFiles() {
        val cacheDir = getActivity()?.getCacheDir()
        val tempFile = File(cacheDir, "tempFile")
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }


}

