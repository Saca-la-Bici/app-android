package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.databinding.ActivityAnnouncementsBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class AnnouncementsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAnnouncementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()

    }

    private fun initializeBinding(){
        binding = ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupNavbar() {
        findViewById<ImageButton>(R.id.btnActividades).setOnClickListener {
            val intent = Intent(this, ActivitiesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.btnPerfil).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.btnMapa).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.btnAnuncios).setOnClickListener {
            val intent = Intent(this, AnnouncementsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}