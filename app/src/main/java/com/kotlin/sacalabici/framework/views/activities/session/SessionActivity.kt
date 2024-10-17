@file:Suppress("DEPRECATION")
/**
 * File: SessionActivity.kt
 * Description: Actividad principal de sesión de usuario, permite al usuario iniciar sesión con Google, Facebook,
 *              o navegar a la pantalla de inicio de sesión o registro. Observa los estados de autenticación y
 *              responde a eventos como inicio de sesión exitoso o errores.
 * Date: 16/10/2024
 * Changes:
 */

package com.kotlin.sacalabici.framework.views.activities.session

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivitySessionBinding
import com.kotlin.sacalabici.framework.viewmodel.session.AuthViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.utils.Constants

/**
 * Actividad que gestiona la sesión del usuario y proporciona opciones para iniciar sesión con Google o Facebook,
 * así como navegar a las pantallas de inicio de sesión y registro. También observa y maneja cambios en el estado
 * de autenticación del usuario.
 */
class SessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeAuthViewModel()
        setupButtonListeners()
        observeAuthState()
    }

    /**
     * Observa los cambios en el estado de autenticación y ejecuta las acciones correspondientes, como navegar a la pantalla
     * principal, mostrar mensajes de error, o redirigir a la finalización del perfil si es necesario.
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
     * Navega a la actividad especificada y cierra la actual.
     * @param activity Clase de la actividad a la que se desea navegar.
     */
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

    /**
     * Configura los listeners para los botones de la interfaz, incluyendo inicio de sesión con Google, Facebook,
     * y la navegación a las pantallas de inicio de sesión o registro.
     */
    private fun setupButtonListeners() {
        binding.BGoogle.setOnClickListener { authViewModel.signInWithGoogle(this) }
        binding.BFacebook.setOnClickListener { authViewModel.signInWithFacebook(this) }
        binding.BLogin.setOnClickListener { navigateTo(LoginActivity::class.java) }
        binding.BRegister.setOnClickListener { navigateTo(RegisterUserActivity::class.java) }
    }

    /**
     * Maneja los resultados de las actividades de inicio de sesión con Google y Facebook, llamando a los métodos
     * correspondientes del ViewModel para gestionar la respuesta de los servicios de autenticación.
     * @param requestCode Código de solicitud de la actividad
     * @param resultCode Código de resultado devuelto por la actividad
     * @param data Intent que contiene los datos devueltos por la actividad
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

    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        authViewModel.startAuthStateListener()
    }
}