package com.kotlin.sacalabici.framework.views.activities.announcement

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.databinding.ActivityModifyAnnouncementBinding
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ModifyAnnouncementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyAnnouncementBinding
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var originalImageUrl: String? = null
    private var isImageErased: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]

        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        originalImageUrl = intent.getStringExtra("url")
        val imagen = originalImageUrl

        populateUI(id, title, content, originalImageUrl)
        initializeListeners()
        registerImagePicker()
    }

    private fun populateUI(id: String?, title: String?, content: String?, url: String?) {
        binding.etModifyAnnouncementTitle.setText(title)
        binding.etModifyAnnouncementDescription.setText(content)
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
                .load(url)
                .into(binding.ibAddImage)
        }
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
                val id = intent.getStringExtra("id") ?: ""
                val title = binding.etModifyAnnouncementTitle.text.toString()
                val description = binding.etModifyAnnouncementDescription.text.toString()

                showLoading(true)

                lifecycleScope.launch {
                    // Si se ha seleccionado una nueva imagen, usamos ese Uri. De lo contrario, descargamos la imagen original.
                    val imageUri = when {
                        isImageErased -> null
                        selectedImageUri != null -> selectedImageUri
                        !originalImageUrl.isNullOrEmpty() && !isImageErased -> downloadImageToFile(originalImageUrl!!)
                        else -> null
                    }

                    // Creamos el objeto Announcement con el Uri resultante
                    val announcement = Announcement(title, description, imageUri)

                    // Enviamos el anuncio modificado al ViewModel
                    viewModel.patchAnnouncement(id, announcement, this@ModifyAnnouncementActivity) { result ->
                        showLoading(false)
                        result.fold(
                            onSuccess = {
                                Toast.makeText(this@ModifyAnnouncementActivity, "Anuncio modificado exitosamente", Toast.LENGTH_SHORT).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            },
                            onFailure = { error ->
                                Toast.makeText(this@ModifyAnnouncementActivity, "Error al modificar el anuncio: ${error.message}", Toast.LENGTH_LONG).show()
                                Log.e("ModifyAnnouncement", "Error: ${error.message}")
                            }
                        )
                    }
                }
            }
        }
        binding.ibAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun validateFields(): Boolean {
        if (binding.etModifyAnnouncementTitle.text.isNullOrEmpty()) {
            binding.etModifyAnnouncementTitle.error = "El título no puede estar vacío"
            return false
        }
        if (binding.etModifyAnnouncementDescription.text.isNullOrEmpty()) {
            binding.etModifyAnnouncementDescription.error = "La descripción no puede estar vacía"
            return false
        }
        return true
    }


    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.ibAddImage.setImageURI(selectedImageUri)
            }
        }
    }

    private suspend fun downloadImageToFile(url: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                // Abrimos un stream para la URL remota
                val input = URL(url).openStream()
                // Creamos un archivo temporal en el directorio de caché
                val file = File(cacheDir, "temp_image.jpg")
                val output = FileOutputStream(file)
                // Copiamos los bytes de la imagen al archivo local
                input.copyTo(output)
                // Cerramos el stream de salida
                output.close()
                // Devolvemos el Uri del archivo local
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanTemporaryFiles()
    }

    private fun cleanTemporaryFiles() {
        val tempFile = File(cacheDir, "tempFile")
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ibCheck.isEnabled = !isLoading
    }

    private fun eraseImage() {
        selectedImageUri = null
        isImageErased = true
        binding.ibAddImage.setImageResource(R.drawable.ic_add_image)
    }

}
