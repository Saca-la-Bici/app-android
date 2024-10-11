@file:Suppress("DEPRECATION")
package com.kotlin.sacalabici.framework.views.activities.session
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivityLoginBinding
import com.kotlin.sacalabici.framework.viewmodel.session.AuthViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import com.kotlin.sacalabici.framework.views.activities.session.RecoverPasswordActivity
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
                .requestIdToken(Constants.REQUEST_ID_TOKEN)
                .requestEmail()
                .build(),
            this
        )
        // Observe registration state
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                is AuthState.CompleteProfile -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }
        // Listeners para los botones
        binding.TVForgotPassword.setOnClickListener{
            val intent=Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.BSession.setOnClickListener {
            val email = binding.TILEmail.editText?.text.toString()
            val password = binding.TILPassword.editText?.text.toString()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.BSession.isEnabled = true
            }, 5000)
            if (!isValidEmail(email)) {
                binding.BSession.isEnabled = false
                binding.TILEmail.error = "Por favor ingresa un correo electrónico válido"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.BSession.isEnabled = false
                binding.TILPassword.error = "Por favor ingresa una contraseña"
                return@setOnClickListener
            }
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
            finish()
        }
    }
    private fun initializeBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
}