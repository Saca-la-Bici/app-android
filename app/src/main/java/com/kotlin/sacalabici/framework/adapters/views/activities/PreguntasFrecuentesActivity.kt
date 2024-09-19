package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityPreguntasFrecuentesBinding

class PreguntasFrecuentesActivity: BaseActivity() {
    private lateinit var binding: ActivityPreguntasFrecuentesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupNavbar()
    }
    private fun initializeBinding(){
        binding = ActivityPreguntasFrecuentesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}