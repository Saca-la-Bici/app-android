package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import com.kotlin.sacalabici.databinding.ActivityProfileEditBinding



class ProfileEditActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
    }

    private fun initializeBinding(){
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}