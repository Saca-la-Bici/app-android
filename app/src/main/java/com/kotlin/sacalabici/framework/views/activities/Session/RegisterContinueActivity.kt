package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivityRegisterUserBinding
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.RegisterContinueViewModel
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.RegisterFinishViewModel

class RegisterContinueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserContinueBinding
    private val registerContinueViewModel: RegisterContinueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        val email = intent.getStringExtra("email")
        val username =intent.getStringExtra("username")
        val name = intent.getStringExtra("name")

        binding.BBack.setOnClickListener {
            val intent = Intent(this@RegisterContinueActivity, RegisterUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.BContinue.setOnClickListener {
            val password = binding.TILPassword.editText?.text.toString()
            val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()

            val errorMessage = registerContinueViewModel.arePasswordsValid(password, confirmPassword)
            if (errorMessage != null) {
                Toast.makeText(this@RegisterContinueActivity, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@RegisterContinueActivity, RegisterFinishActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("username", username)
                intent.putExtra("name", name)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserContinueBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
