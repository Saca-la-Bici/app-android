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
import okhttp3.internal.threadName


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

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false) // Binding de la vista
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val root: View = binding.root

        // Llamadas a los métodos para dar funcionalidad a los elementos de la visata
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfile().observe(viewLifecycleOwner) { profile -> // Al crear la vista se manda a llamar la función para obtener la información del usuario
            profile?.let {
                binding.username.setText(profile.user) // Se poblan los campos con la información del usuario.
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


    private fun setupBloodDropdown() {  // Poblar al dropdown de selección de tipo de sangre con las opciones.
        val bloodDropdownConfig = binding.bloodDropDown
        val bloodTypes = resources.getStringArray(R.array.bloodTypes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, bloodTypes)
        bloodDropdownConfig.setAdapter(arrayAdapter)
    }


    private fun setupBackButton() {     // Dar funcionalidad al botón para regresar a consultar perfil sin publicar los cambios.
        val backButton = binding.btnBack
        backButton.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, profileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupUploadButton() {   // Dar funcionalidad al botón para publicar los cambios.
        val saveButton = binding.btnSave
        saveButton.setOnClickListener {
            val valid = inputValidation() // Varible que define si los campos fueron llenados correctamente.
            val image = selectedImageUri    // Tomamos el valor actual de cada elemento de la vista y lo guardamos en un avariable
            val name = binding.name.text.toString()
            val username = binding.username.text.toString()
            val blood = binding.bloodDropDown.text.toString()
            val emergencyNum = binding.emergencyNumber.text.toString()
            val profile = Profile(username, name, blood, emergencyNum, 0, 0, 0.0, image, 0,) //Poblamos el objeto de tipo Profile con el que mandaremos todos los datos.
            val context: Context = requireContext()

            if(valid){ // Continuar si los datos son validos
                lifecycleScope.launch {
                    val success = viewModel.patchProfile(profile, context) // Llama la función de patchProfile del viewmodel con los datos.
                    if (success) { // Si se publican los cambios de manera exitosa, se devuelve a la vista de consultar perfil.
                        val profileFragment = ProfileFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, profileFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setUpEditImageButton(){ // Funcionalidad para abrir el selector de imagen.
        val imageButton = binding.profileImageLayout
        imageButton.setOnClickListener{
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageLauncher.launch(intent)
        }
    }

    private fun registerImagePicker() { // Funcionalidad para usar la imagen seleccionada.
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.profileImage.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onDestroy() { // Función para borrar la cache con la imagen que se subió al salir de la vista.
        super.onDestroy()
        cleanTemporaryFiles()
    }

    private fun cleanTemporaryFiles() { // Función para borrar la cache con la imagen que se subió.
        val cacheDir = getActivity()?.getCacheDir()
        val tempFile = File(cacheDir, "tempFile")
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    private fun inputValidation(): Boolean{ // Función que se manda a llamar al publicar lso cambios para validar todos los campos que se llenaron.
        val nameBinding = binding.name
        val usernameBinding = binding.username
        val numberBinding = binding.emergencyNumber

        val name = nameBinding.getText().toString();
        if (name.trim().isEmpty()){
            Toast.makeText(activity, "Nombre no debe estar vacío", Toast.LENGTH_LONG).show()
            return false
        }

        val username = usernameBinding.getText().toString();
        if (username.trim().isEmpty()){
            Toast.makeText(activity, "Nombre de usuario no debe estar vacío", Toast.LENGTH_LONG).show()
            return false
        }

        val number = numberBinding.getText().toString();
        if (number.trim().isEmpty()){
            Toast.makeText(activity, "Número de emergencia no debe estar vacío", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

}

