package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding

class RegisterContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterUserContinueBinding
    //private val viewModel: RegisterContinueViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        initializeBinding()

        binding.BFinish.setOnClickListener {
            var email = intent.getStringExtra("email")
            var username = intent.getStringExtra("username")
            var birthday = intent.getStringExtra("birthday")
            val password = binding.TILPassword.editText?.text.toString()
            val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()

            if (arePasswordsValid(password, confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email!!, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)

                            // User creation successful
                            //val firebaseUser = firebaseAuth.currentUser
                            //email = firebaseUser!!.email
                            //Toast.makeText(this,"Account created with email $email",Toast.LENGTH_SHORT).show()

                            // ... (store additional user data in MongoDB)
                            // ... (proceed to next step or redirect)
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, ActivitiesActivity::class.java)
                            // Handle error (e.g., display error message)
                            Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                        }
                    }
            } else {
                // Show error message for invalid passwords
                Toast.makeText(this, "Passwords do not match or are too short", Toast.LENGTH_SHORT).show()
            }
        }

        binding.BBack.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun arePasswordsValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6 // Example criteria
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserContinueBinding.inflate(layoutInflater)
        setContentView(binding.root)}
}