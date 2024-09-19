package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import com.kotlin.sacalabici.databinding.ActivityRegisterannouncementBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.AnnouncementsViewModel

class AddAnnouncementActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterannouncementBinding
    private lateinit var viewModel: AnnouncementsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.ibClose.setOnClickListener {
            finish()
        }
        binding.ibCheck.setOnClickListener {
            val title = binding.etAddAnnouncementTitle.text.toString()
            val description = binding.etAddAnnouncementDescription.text.toString()
            val image = "path/test"
            val annnouncement = Announcement(title, description, image)
            viewModel.postAnnouncement(annnouncement)
            finish()
        }

    }

    private fun initializeBinding() {
        binding = ActivityRegisterannouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}