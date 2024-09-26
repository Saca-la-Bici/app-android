package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.RegisterViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity

class RegisterContinueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserContinueBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        // Initialize ViewModel
        registerViewModel.initialize(FirebaseAuth.getInstance())

        // Observe registration state
        registerViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    // Registration successful
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: Finish RegisteerContinueActivity to prevent going back
                }
                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }

                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }

        binding.BFinish.setOnClickListener {
            val email = intent.getStringExtra("email") ?: ""
            val username = intent.getStringExtra("username") ?: ""
            val fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: ""
            val password = binding.TILPassword.editText?.text.toString()
            val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.BFinish.isEnabled = true
            }, 5000)

            if (password != confirmPassword) {
                binding.BFinish.isEnabled = false
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerViewModel.registerUser(email, password, confirmPassword, username, fechaNacimiento)
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
