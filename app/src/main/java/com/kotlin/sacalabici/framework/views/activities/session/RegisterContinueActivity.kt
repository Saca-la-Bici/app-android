/**
 * File: RegisterContinueActivity.kt
 * Description: Actividad intermedia para completar el proceso de registro de usuario, donde se ingresan
 *              las contraseñas y se validan antes de continuar con el registro.
 * Date: 16/10/2024
 * Changes:
 */

package com.kotlin.sacalabici.framework.views.activities.session
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding
import com.kotlin.sacalabici.framework.viewmodel.session.RegisterContinueViewModel

/**
 * Actividad que permite al usuario continuar con el proceso de registro ingresando y validando su contraseña.
 * Si las contraseñas son válidas, se procede a la siguiente pantalla de registro.
 */
class RegisterContinueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserContinueBinding
    private val registerContinueViewModel: RegisterContinueViewModel by viewModels()
    private var email: String? = null
    private var username: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeExtras()
        setupButtonListeners()
    }

    /**
     * Navega a la actividad proporcionada y finaliza la actividad actual.
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
     * Configura los listeners para los botones de la interfaz, incluyendo el botón de regreso
     * y el botón de continuar que valida las contraseñas.
     */
    private fun setupButtonListeners() {
        binding.BBack.setOnClickListener { navigateTo(SessionActivity::class.java) }
        binding.BContinue.setOnClickListener { setupContinueButton() }
    }

    /**
     * Valida las contraseñas ingresadas por el usuario. Si son válidas, navega a la actividad de
     * finalización del registro, pasando los datos necesarios a la siguiente pantalla.
     * Deshabilita temporalmente el botón de continuar para evitar múltiples envíos.
     */
    private fun setupContinueButton() {
        val password = binding.TILPassword.editText?.text.toString()
        val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.BContinue.isEnabled = true
        }, 5000)
        val errorMessage = registerContinueViewModel.arePasswordsValid(password, confirmPassword)
        if (errorMessage != null) {
            binding.BContinue.isEnabled = false
            Toast.makeText(this@RegisterContinueActivity, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this@RegisterContinueActivity, RegisterFinishActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("username", username)
            intent.putExtra("name", name)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }

    /**
     * Inicializa las variables `email`, `username` y `name` utilizando los valores pasados
     * a través de `intent` desde la actividad anterior.
     */
    private fun initializeExtras() {
        email = intent.getStringExtra("email")
        username = intent.getStringExtra("username")
        name = intent.getStringExtra("name")
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserContinueBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}