/**
 * File: RegisterUserActivity.kt
 * Description: Actividad de registro inicial de usuario donde se capturan datos como correo electrónico,
 *              nombre de usuario y nombre completo. Luego, continúa con el proceso de registro.
 * Date: 16/10/2024
 * Changes:
 */

package com.kotlin.sacalabici.framework.views.activities.session
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.databinding.ActivityRegisterUserBinding
import com.kotlin.sacalabici.framework.viewmodel.session.RegisterUserViewModel
import kotlinx.coroutines.launch
class RegisterUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterUserBinding
    private val registerUserViewModel: RegisterUserViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupButtonListeners()
    }

    /**
     * Navega a la actividad especificada y finaliza la actividad actual.
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
     * Configura los listeners para los botones de la interfaz. El botón de retroceso navega a la actividad
     * de sesión, mientras que el botón de continuar valida la información del usuario y navega a la siguiente
     * actividad de registro.
     */
    private fun setupButtonListeners() {
        binding.BBack.setOnClickListener { navigateTo(SessionActivity::class.java) }
        binding.BContinue.setOnClickListener { setupContinueButton() }
    }

    /**
     * Valida la información proporcionada por el usuario (correo electrónico, nombre de usuario, nombre completo)
     * y navega a la siguiente pantalla de registro si no hay errores. Si hay errores, se muestran mensajes apropiados
     * en los campos correspondientes.
     */
    private fun setupContinueButton() {
        val email = binding.TILEmail.editText?.text.toString()
        val username = binding.TILUsername.editText?.text.toString()
        val name = binding.TILName.editText?.text.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.BContinue.isEnabled = true
        }, 5000)
        lifecycleScope.launch {
            val errorMessage = registerUserViewModel.validate(email, username, name)
            if (errorMessage != null) {
                binding.BContinue.isEnabled = false
                Toast.makeText(this@RegisterUserActivity, errorMessage, Toast.LENGTH_SHORT).show()
                when {
                    errorMessage.contains("correo electrónico") -> {
                        binding.TILEmail.error = errorMessage
                    }
                    errorMessage.contains("nombre de usuario") -> {
                        binding.TILUsername.error = errorMessage
                    }
                    errorMessage.contains("nombre completo") -> {
                        binding.TILName.error = errorMessage
                    }
                }
            } else {
                Log.d("RegisterUserActivity", "Email: $email, Username: $username, Name: $name")
                val intent = Intent(this@RegisterUserActivity, RegisterContinueActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("username", username)
                intent.putExtra("name", name)
                startActivity(intent)
            }
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}