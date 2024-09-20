package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import com.kotlin.sacalabici.databinding.ActivityFaqsBinding

class FaqsActivity: BaseActivity() {
    private lateinit var binding: ActivityFaqsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupNavbar()
    }
    private fun initializeBinding(){
        binding = ActivityFaqsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}