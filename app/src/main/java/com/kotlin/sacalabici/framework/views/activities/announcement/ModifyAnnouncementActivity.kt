package com.kotlin.sacalabici.framework.views.activities.announcement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.databinding.ActivityModifyAnnouncementBinding
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel

class ModifyAnnouncementActivity: AppCompatActivity() {
    private lateinit var binding: ActivityModifyAnnouncementBinding
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_announcement)
        initializeBinding()

        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val url = intent.getStringExtra("url")
        populateUI(id, title, content, url)


        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        initializeListeners()
        registerImagePicker()
    }

    private fun populateUI(id: String?, title: String?, content: String?, url: String?) {
        binding.etModifyAnnouncementTitle.setText(title)
        binding.etModifyAnnouncementDescription.setText(content)
    }

    private fun initializeListeners() {
        binding.ibClose.setOnClickListener {
            finish()
        }
        binding.ibCheck.setOnClickListener {
            val emptystring = ""
            val id = intent.getStringExtra("id") ?: emptystring
            val title = binding.etModifyAnnouncementTitle.text.toString()
            val description = binding.etModifyAnnouncementDescription.text.toString()
            val image = emptystring.takeIf { it.isNotEmpty() }
            val announcement = Announcement(title, description, image)
            viewModel.putAnnouncement(id, announcement)
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.ibAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivityModifyAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                Log.d("ImagePicker", "Selected image URI: $selectedImageUri")
                /*TODO: Implement image upload*/
            }
        }
    }

}