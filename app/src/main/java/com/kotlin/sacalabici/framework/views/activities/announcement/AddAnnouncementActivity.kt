package com.kotlin.sacalabici.framework.views.activities.announcement

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.databinding.ActivityRegisterannouncementBinding
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel
import java.io.File

class AddAnnouncementActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterannouncementBinding
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        initializeListeners()
        registerImagePicker()
    }

    private fun initializeListeners() {
        binding.ibClose.setOnClickListener {
            finish()
        }
        binding.ibCheck.setOnClickListener {
            if(validateFields()){
                val title = binding.etAddAnnouncementTitle.text.toString()
                val description = binding.etAddAnnouncementDescription.text.toString()
                val image = selectedImageUri
                val annnouncement = Announcement(title, description, image)
                viewModel.postAnnouncement(annnouncement, this)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        binding.ibAddImage.setOnClickListener { // Llamar al botón que abre la galería de imágenes
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun validateFields(): Boolean {
        if (binding.etAddAnnouncementTitle.text.isNullOrEmpty()) {
            binding.etAddAnnouncementTitle.error = "El título no puede estar vacío"
            return false
        } else {
            binding.etAddAnnouncementTitle.error = null
        }
        if (binding.etAddAnnouncementDescription.text.isNullOrEmpty()) {
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

    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.ibAddImage.setImageURI(selectedImageUri)
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
}