package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivitySettingsBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel


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