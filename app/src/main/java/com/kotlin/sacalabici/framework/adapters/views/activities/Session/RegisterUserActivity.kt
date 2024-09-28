package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.databinding.ActivityRegisterUserBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.RegisterUserViewModel
import kotlinx.coroutines.launch

class RegisterUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterUserBinding
    private val registerUserViewModel: RegisterUserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        binding.BBack.setOnClickListener {
            val intent = Intent(this@RegisterUserActivity, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.BContinue.setOnClickListener {
            val email = binding.TILEmail.editText?.text.toString()
            val username = binding.TILUsername.editText?.text.toString()
            val name = binding.TILName.editText?.text.toString()

            lifecycleScope.launch {
                val errorMessage = registerUserViewModel.validate(email, username, name)
                if (errorMessage != null) {
                    Toast.makeText(this@RegisterUserActivity, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("RegisterUserActivity", "Email: $email, Username: $username, Name: $name")
                    val intent = Intent(this@RegisterUserActivity, RegisterContinueActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("username", username)
                    intent.putExtra("name", name)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}