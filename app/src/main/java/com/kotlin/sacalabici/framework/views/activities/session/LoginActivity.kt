/**
 * File: LoginActivity.kt
 * Description: Esta clase gestiona el inicio de sesión de los usuarios mediante diferentes métodos
 *              como Google, Facebook o correo electrónico y contraseña. También permite la recuperación
 *              de contraseñas olvidadas. La autenticación se maneja a través de Firebase.
 * Date: 16/10/2024
 * Changes:
 */

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
import com.kotlin.sacalabici.utils.Constants

/**
 * Esta actividad maneja el proceso de inicio de sesión de usuarios utilizando Google, Facebook o correo electrónico
 * y contraseña. También proporciona la opción de recuperación de contraseñas olvidadas. Se observan los cambios
 * de estado de autenticación y se redirige al usuario según el estado actual.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeAuthViewModel()
        setupButtonListeners()
        observeAuthState()
    }

    /**
     * Observa los cambios en el estado de autenticación del ViewModel y redirige al usuario a la actividad
     * correspondiente dependiendo del estado (éxito, error, perfil incompleto, etc.).
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
     * Navega hacia una actividad específica eliminando las actividades anteriores de la pila de tareas.
     * @param activity Clase de la actividad a la que se desea navegar.
     */
    private fun navigateTo(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Inicializa el ViewModel de autenticación con FirebaseAuth y las opciones de inicio de sesión de Google.
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
     * Configura los listeners para los botones de inicio de sesión con Google, Facebook, y correo electrónico
     * y contraseña, así como el botón de recuperación de contraseña.
     */
    private fun setupButtonListeners() {
        binding.BGoogle.setOnClickListener { authViewModel.signInWithGoogle(this) }
        binding.BFacebook.setOnClickListener { authViewModel.signInWithFacebook(this) }
        binding.BBack.setOnClickListener { navigateTo(SessionActivity::class.java) }
        binding.TVForgotPassword.setOnClickListener {
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
    }

    private fun initializeBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Manejador de resultados de actividad que se utiliza para manejar la respuesta de la autenticación
     * de Google o Facebook cuando se llama a `startActivityForResult`.
     * @param requestCode Código de solicitud de la actividad.
     * @param resultCode Código de resultado devuelto por la actividad.
     * @param data Intent con los datos devueltos.
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
     * Verifica si el correo electrónico proporcionado tiene un formato válido utilizando un patrón predefinido.
     * @param email Correo electrónico que se desea validar.
     * @return `true` si el correo es válido, `false` de lo contrario.
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}