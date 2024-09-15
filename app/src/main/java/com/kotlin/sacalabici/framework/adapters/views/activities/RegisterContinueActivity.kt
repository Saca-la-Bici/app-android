package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.RegisterContinueViewModel
import com.kotlin.sacalabici.framework.adapters.viewmodel.RegistrationState

class RegisterContinueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserContinueBinding
    private val registerViewModel: RegisterContinueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        // Initialize ViewModel
        registerViewModel.initialize(FirebaseAuth.getInstance())

        // Observe registration state
        registerViewModel.registrationState.observe(this) { registrationState ->
            when (registrationState) {
                is RegistrationState.Success -> {
                    // Registration successful
                    val intent = Intent(this, ActivitiesActivity::class.java)
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
                is RegistrationState.Error -> {
                    Toast.makeText(this, registrationState.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.BFinish.setOnClickListener {
            val email = intent.getStringExtra("email") ?: ""
            val username = intent.getStringExtra("username") ?: ""
            val birthday = intent.getStringExtra("birthday") ?: ""
            val password = binding.TILPassword.editText?.text.toString()
            val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()

            registerViewModel.registerUser(email, password, confirmPassword)
        }

        binding.BBack.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserContinueBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
