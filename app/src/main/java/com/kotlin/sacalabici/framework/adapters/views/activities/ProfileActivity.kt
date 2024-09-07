package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.databinding.ActivityProfileBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class ProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()

    }

    private fun initializeBinding(){
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupNavbar() {
        val currentActivity = this::class.java.simpleName
        val btnActividades = findViewById<ImageButton>(R.id.btnActividades)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val btnMapa = findViewById<ImageButton>(R.id.btnMapa)
        val btnAnuncios = findViewById<ImageButton>(R.id.btnAnuncios)

        val tvActividades = findViewById<TextView>(R.id.tvActividades)
        val tvPerfil = findViewById<TextView>(R.id.tvPerfil)
        val tvMapa = findViewById<TextView>(R.id.tvMapa)
        val tvAnuncios = findViewById<TextView>(R.id.tvAnuncios)

        btnActividades.setOnClickListener {
            val intent = Intent(this, ActivitiesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        btnPerfil.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        btnMapa.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        btnAnuncios.setOnClickListener {
            val intent = Intent(this, AnnouncementsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // Set highlight for current activity
        when (currentActivity) {
            "ActivitiesActivity" -> {
                btnActividades.setImageResource(R.drawable.ic_actividades_selected)
                btnActividades.background = getDrawable(R.drawable.bg_highlight_black)
                tvActividades.setTextColor(Color.BLACK)
            }
            "ProfileActivity" -> {
                btnPerfil.setImageResource(R.drawable.ic_perfil_selected)
                btnPerfil.background = getDrawable(R.drawable.bg_highlight_black)
                tvPerfil.setTextColor(Color.BLACK)
            }
            "MapActivity" -> {
                btnMapa.setImageResource(R.drawable.ic_mapa_selected)
                btnMapa.background = getDrawable(R.drawable.bg_highlight_black)
                tvMapa.setTextColor(Color.BLACK)
            }
            "AnnouncementsActivity" -> {
                btnAnuncios.setImageResource(R.drawable.ic_anuncios_selected)
                btnAnuncios.background = getDrawable(R.drawable.bg_highlight_black)
                tvAnuncios.setTextColor(Color.BLACK)
            }
        }
    }
}
