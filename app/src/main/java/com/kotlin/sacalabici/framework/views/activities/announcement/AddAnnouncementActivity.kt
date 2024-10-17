package com.kotlin.sacalabici.framework.views.activities.announcement

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.databinding.ActivityRegisterannouncementBinding
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel
import java.io.File

class AddAnnouncementActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterannouncementBinding
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var isImageErased: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        initializeListeners()
        registerImagePicker()
        setupTextWatchers()
    }

    private fun initializeListeners() {
        binding.ibClose.setOnClickListener {
            finish()
        }
        binding.tvEraseImage.setOnClickListener {
            eraseImage()
        }

        binding.ibCheck.setOnClickListener {
            if (validateFields()) {
                val title = binding.etAddAnnouncementTitle.text.toString()
                val description = binding.etAddAnnouncementDescription.text.toString()
                val image = selectedImageUri
                val announcement = Announcement(title, description, image)

                // Mostrar progreso
                showLoading(true)

                // Publica el anuncio utilizando el ViewModel
                viewModel.postAnnouncement(announcement, this) { result ->
                    runOnUiThread {
                        showLoading(false)
                        result.fold(
                            onSuccess = {
                                Toast.makeText(this, "Anuncio registrado exitosamente", Toast.LENGTH_SHORT).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            },
                            onFailure = { error ->
                                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }

        // Listener para el botón de agregar imagen
        binding.ibAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    // Método para validar campos de entrada
    private fun validateFields(): Boolean {
        if (binding.etAddAnnouncementTitle.text.isNullOrBlank()) {
            binding.etAddAnnouncementTitle.error = "El título no puede estar vacío"
            return false
        } else {
            binding.etAddAnnouncementTitle.error = null
        }
        if (binding.etAddAnnouncementDescription.text.isNullOrBlank()) {
            binding.etAddAnnouncementDescription.error = "La descripción no puede estar vacía"
            return false
        } else {
            binding.etAddAnnouncementDescription.error = null
        }
        return true
    }

    private fun initializeBinding() {
        binding = ActivityRegisterannouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Método para registrar el lanzador de actividad para seleccionar imágenes
    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.ibAddImage.apply{
                    setImageURI(selectedImageUri)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                }
            }
        }
    }

    // Limpia los archivos temporales al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        cleanTemporaryFiles()
    }

    // Método para limpiar archivos temporales
    private fun cleanTemporaryFiles() {
        val tempFile = File(cacheDir, "tempFile")
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    // Método para mostrar u ocultar el indicador de carga
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ibCheck.isEnabled = !isLoading
    }

    // Método para borrar la imagen seleccionada
    private fun eraseImage() {
        selectedImageUri = null
        isImageErased = true
        binding.ibAddImage.apply{
            setImageResource(R.drawable.ic_add_image)
            scaleType = ImageView.ScaleType.CENTER
            adjustViewBounds = false
        }
    }

    // Configura los TextWatchers para los campos de texto
    private fun setupTextWatchers() {
        binding.etAddAnnouncementDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tvAddAnnouncementDescriptionCounter.text = "${s?.length ?: 0}/500"
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvAddAnnouncementDescriptionCounter.text = "${s?.length ?: 0}/500"
            }
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 500) {
                    Toast.makeText(this@AddAnnouncementActivity, "Has alcanzado el límite de 500 caracteres", Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.etAddAnnouncementTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tvAddAnnouncementTitleCounter.text = "${s?.length ?: 0}/100"
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvAddAnnouncementTitleCounter.text = "${s?.length ?: 0}/100"
            }
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 100) {
                    Toast.makeText(this@AddAnnouncementActivity, "Has alcanzado el límite de 100 caracteres", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}