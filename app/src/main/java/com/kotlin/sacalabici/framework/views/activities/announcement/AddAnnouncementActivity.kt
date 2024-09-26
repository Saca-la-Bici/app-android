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
            val title = binding.etAddAnnouncementTitle.text.toString()
            val description = binding.etAddAnnouncementDescription.text.toString()
            val image = selectedImageUri.toString()
            val annnouncement = Announcement(title, description, image)
            viewModel.postAnnouncement(annnouncement)
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.ibAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterannouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                Log.d("ImagePicker", "Selected image URI: $selectedImageUri")
                /*TODO: Implement image upload*/
            }
        }
    }

}