package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivitySessionBinding

class SessionActivity: AppCompatActivity() {
    lateinit var binding: ActivitySessionBinding
//    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()

        binding.BLogin.setOnClickListener {
//                viewModel.login()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.BRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}