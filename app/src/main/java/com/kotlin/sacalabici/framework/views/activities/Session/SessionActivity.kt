package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivitySessionBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.AuthViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.utils.Constants

class SessionActivity() : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Inicializa el ViewModel con Firebase y GoogleSignInOptions
        authViewModel.initialize(
            FirebaseAuth.getInstance(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.REQUEST_ID_TOKEN)
                .requestEmail()
                .build(),
            this
        )

        // Observe registration state
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    // Registration successful
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish() // Optional: Finish RegisteerContinueActivity to prevent going back
                }

                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.IncompleteProfile -> {
                    val intent = Intent(this, LoginFinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthState.CompleteProfile -> {
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Unauthenticated -> {
                    Log.d("SessionActivity", "Usuario no autenticado")
                }

                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }

        // Listeners para los botones
        binding.BGoogle.setOnClickListener {
            authViewModel.signInWithGoogle(this)
        }

        binding.BFacebook.setOnClickListener {
            authViewModel.signInWithFacebook(this)
        }

        binding.BLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.BRegister.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
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
        authViewModel.startAuthStateListener()
    }
}

