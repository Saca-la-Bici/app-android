package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivitySettingsBinding



class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
    }

    private fun initializeBinding(){
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}