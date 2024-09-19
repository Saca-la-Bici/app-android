package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.databinding.ActivityLoginBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.AuthState
import com.kotlin.sacalabici.framework.adapters.viewmodel.AuthViewModel
import com.kotlin.sacalabici.utils.Constants

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        // Initialize ViewModel
        authViewModel.initialize(
            FirebaseAuth.getInstance(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1077042273728-8dgeo63mtbl5e25n0b6n453ptd7fhm9a.apps.googleusercontent.com")
                .requestEmail()
                .build(),
            this
        )

        // Observe authentication state
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    // Handle successful login
                    val intent = Intent(this, ActivitiesActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: Finish LoginActivity to prevent going back
                }
                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                AuthState.Cancel -> {
                    Toast.makeText(this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Listeners para los botones
        binding.BSession.setOnClickListener {
            val email = binding.TILEmail.editText?.text.toString()
            val password = binding.TILPassword.editText?.text.toString()
            authViewModel.signInWithEmailAndPassword(email, password)
        }

        binding.BGoogle.setOnClickListener {
            authViewModel.signInWithGoogle(this)
        }

        binding.BFacebook.setOnClickListener {
            authViewModel.signInWithFacebook(this)
        }

        binding.BBack.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authViewModel.getCallbackManager().onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            authViewModel.handleGoogleSignInResult(task)
        }
    }

    override fun onStart() {
        super.onStart()
        authViewModel.checkCurrentUser()
    }
}
