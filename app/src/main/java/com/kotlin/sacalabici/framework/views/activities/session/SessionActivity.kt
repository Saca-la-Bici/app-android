@file:Suppress("DEPRECATION")
package com.kotlin.sacalabici.framework.views.activities.session

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivitySessionBinding
import com.kotlin.sacalabici.framework.viewmodel.session.AuthViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.utils.Constants
class SessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeAuthViewModel()
        setupButtonListeners()
        observeAuthState()
        // Setup WebView and close button (ShapeableImageView)
        val webView: WebView = findViewById(R.id.webView)
        val closeWebView: ShapeableImageView = findViewById(R.id.closeWebView)  // Cast as ShapeableImageView

        // Configure WebView to open links within the app
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true // Enable JavaScript if necessary
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true


        // Listener for Privacy Policy link
        binding.TVPrivacyPolicy.setOnClickListener {
            val url = "http://18.220.205.53:8080/politicasAplicacion/politicaPrivacidad"
            webView.loadUrl(url)
            webView.visibility = View.VISIBLE
            closeWebView.visibility = View.VISIBLE
        }

        // Listener for Privacy Policy link
        binding.TVTermsAndConditions.setOnClickListener {
            val url = "http://18.220.205.53:8080/politicasAplicacion/terminosCondiciones"
            webView.loadUrl(url)
            webView.visibility = View.VISIBLE
            closeWebView.visibility = View.VISIBLE
        }

        // Listener for closing the WebView
        closeWebView.setOnClickListener {
            webView.visibility = View.GONE
            closeWebView.visibility = View.GONE
        }
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    navigateTo(MainActivity::class.java)
                }
                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.IncompleteProfile -> {
                    navigateTo(LoginFinishActivity::class.java)
                }
                is AuthState.CompleteProfile -> {
                    navigateTo(MainActivity::class.java)
                }
                is AuthState.VerificationSent -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }
    }


    private fun navigateTo(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun initializeAuthViewModel() {
        authViewModel.initialize(
            FirebaseAuth.getInstance(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.REQUEST_ID_TOKEN)
                .requestEmail()
                .build(),
            this
        )
    }

    private fun setupButtonListeners() {
        binding.BGoogle.setOnClickListener { authViewModel.signInWithGoogle(this) }
        binding.BFacebook.setOnClickListener { authViewModel.signInWithFacebook(this) }
        binding.BLogin.setOnClickListener { navigateTo(LoginActivity::class.java) }
        binding.BRegister.setOnClickListener { navigateTo(RegisterUserActivity::class.java) }
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

    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        authViewModel.startAuthStateListener()
    }
}