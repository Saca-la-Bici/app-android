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

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()

        // Listeners para los botones
        binding.BAsaingRol.setOnClickListener {
            val intent = Intent(this, ConsultarUsuariosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBinding(){
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}
