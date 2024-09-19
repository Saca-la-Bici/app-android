package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import com.kotlin.sacalabici.databinding.ActivitySettingsBinding



class SettingsActivity: BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
    }

    private fun initializeBinding(){
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}