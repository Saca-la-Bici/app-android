/**
 * File: SessionActivity.kt
 * Description: Maneja la actividad de sesión donde los usuarios pueden iniciar sesión a través de diferentes
 *              proveedores (Google, Facebook) y visualizar las políticas de privacidad y términos de servicio.
 * Date: 17/10/2024
 * Changes: Ninguno.
 */

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

/**
 * SessionActivity es responsable de gestionar el inicio de sesión con proveedores externos (Google y Facebook)
 * y de observar el estado de autenticación del usuario. También permite a los usuarios visualizar las políticas
 * de privacidad y los términos y condiciones de la aplicación.
 */
class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private val authViewModel: AuthViewModel by viewModels()

    /**
     * Inicializa la actividad, configurando el ViewModel de autenticación, los listeners de los botones y observando
     * el estado de autenticación del usuario. También configura un WebView para mostrar las políticas de privacidad
     * y los términos y condiciones.
     * @param savedInstanceState Estado guardado de la actividad (opcional).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeAuthViewModel()
        setupButtonListeners()
        observeAuthState()

        // Configuración de WebView para mostrar las políticas de privacidad y los términos de servicio.
        val webView: WebView = findViewById(R.id.webView)
        val closeWebView: ShapeableImageView = findViewById(R.id.closeWebView)

        // Configura el WebView para abrir enlaces dentro de la aplicación.
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true

        // Listener para abrir la política de privacidad.
        binding.TVPrivacyPolicy.setOnClickListener {
            val url = "http://18.220.205.53:8080/politicasAplicacion/politicaPrivacidad"
            webView.loadUrl(url)
            webView.visibility = View.VISIBLE
            closeWebView.visibility = View.VISIBLE
        }

        // Listener para abrir los términos y condiciones.
        binding.TVTermsAndConditions.setOnClickListener {
            val url = "http://18.220.205.53:8080/politicasAplicacion/terminosCondiciones"
            webView.loadUrl(url)
            webView.visibility = View.VISIBLE
            closeWebView.visibility = View.VISIBLE
        }

        // Listener para cerrar el WebView.
        closeWebView.setOnClickListener {
            webView.visibility = View.GONE
            closeWebView.visibility = View.GONE
        }
    }

    /**
     * Observa el estado de autenticación del usuario para determinar el flujo de la aplicación,
     * navegando a diferentes actividades según el estado.
     */
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

    /**
     * Navega a la actividad especificada y finaliza la actividad actual para evitar volver a la pantalla anterior.
     * @param activity Clase de la actividad a la que se debe navegar.
     */
    private fun navigateTo(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Inicializa el ViewModel de autenticación, configurando los proveedores de inicio de sesión como Google.
     */
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

    /**
     * Configura los listeners de los botones para permitir que el usuario inicie sesión con Google,
     * Facebook o navegue a las pantallas de login y registro.
     */
    private fun setupButtonListeners() {
        binding.BGoogle.setOnClickListener { authViewModel.signInWithGoogle(this) }
        binding.BFacebook.setOnClickListener { authViewModel.signInWithFacebook(this) }
        binding.BLogin.setOnClickListener { navigateTo(LoginActivity::class.java) }
        binding.BRegister.setOnClickListener { navigateTo(RegisterUserActivity::class.java) }
    }

    /**
     * Maneja los resultados de las actividades de inicio de sesión para los proveedores externos como Google y Facebook.
     * @param requestCode Código de solicitud enviado a la actividad.
     * @param resultCode Código de resultado devuelto por la actividad.
     * @param data Intent que contiene los datos devueltos por la actividad.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authViewModel.getCallbackManager().onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            authViewModel.handleGoogleSignInResult(task)
        }
    }

    /**
     * Inicializa el binding con el layout de la actividad para poder acceder a los elementos de la interfaz.
     */
    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Inicia el listener de estado de autenticación al arrancar la actividad para observar cualquier cambio
     * en el estado de inicio de sesión del usuario.
     */
    override fun onStart() {
        super.onStart()
        authViewModel.startAuthStateListener()
    }
}
