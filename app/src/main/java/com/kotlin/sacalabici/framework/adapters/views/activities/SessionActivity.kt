package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.kotlin.sacalabici.databinding.ActivitySessionBinding

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    // Registrar el launcher para el resultado de la actividad de inicio de sesión
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Manejar el resultado del intento de inicio de sesión
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            Toast.makeText(this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        configureGoogleSignIn()

        binding.BLogin.setOnClickListener {
            // viewModel.login()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.BRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.BGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun initializeBinding() {
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun configureGoogleSignIn() {
        // Configurar las opciones de inicio de sesión
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Solicita el correo electrónico del usuario
            .build()

        // Crear el cliente de GoogleSignIn
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // El inicio de sesión fue exitoso, puedes obtener la información del usuario aquí
            val email = account?.email
            val displayName = account?.displayName
            val idToken = account?.idToken

            // Aquí puedes manejar la autenticación con tu backend si es necesario
            Toast.makeText(this, "Bienvenido, $displayName", Toast.LENGTH_SHORT).show()

            // Por ejemplo, navegar a otra actividad
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        } catch (e: ApiException) {
            // El inicio de sesión falló, maneja el error
            Log.w("SessionActivity", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onStart() {
//        super.onStart()
//        // Verificar si el usuario ya está firmado en Google
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account != null) {
//            // El usuario ya está firmado en Google, puedes navegar directamente a la siguiente actividad
//            val intent = Intent(this, ActivitiesActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
}
