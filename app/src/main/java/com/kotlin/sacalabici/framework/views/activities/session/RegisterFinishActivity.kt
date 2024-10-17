/**
 * File: RegisterFinishActivity.kt
 * Description: Actividad final para el registro de usuario, donde se completan datos como fecha de nacimiento,
 *              tipo de sangre y número de teléfono antes de finalizar el proceso de registro.
 * Date: 16/10/2024
 * Changes:
 */

package com.kotlin.sacalabici.framework.views.activities.session
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.hbb20.CountryCodePicker
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivityRegisterUserFinishBinding
import com.kotlin.sacalabici.framework.viewmodel.session.RegisterFinishViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Actividad donde el usuario completa su perfil después de registrarse,
 * incluyendo información personal como fecha de nacimiento, tipo de sangre y teléfono.
 */
@Suppress("NAME_SHADOWING")
class RegisterFinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserFinishBinding
    private val registerFinishViewModel: RegisterFinishViewModel by viewModels()
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var countryCodePicker: CountryCodePicker
    private var email: String? = null
    private var username: String? = null
    private var name: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeViewModel()
        initializeExtras()
        observeAuthState()
        setupCountryCodePicker()
        setupBloodTypeDropdown()
        setupButtonListeners()
    }

    /**
     * Observa los cambios en el estado de autenticación y maneja las diferentes transiciones
     * según el estado actual del registro.
     */
    private fun observeAuthState() {
        registerFinishViewModel.authState.observe(this) { authState ->
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
                    binding.BFinish.text = getString(R.string.TFinish)
                }
                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }
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

    private fun initializeViewModel() {
        registerFinishViewModel.initialize(FirebaseAuth.getInstance())
    }

    /**
     * Configura los listeners para los botones de la interfaz, incluyendo el selector de fecha,
     * el botón de retroceso y el botón para finalizar el registro.
     */
    private fun setupButtonListeners() {
        binding.BDate.setOnClickListener { showDatePickerDialog() }
        binding.BBack.setOnClickListener { setupBackButton() }
        binding.BFinish.setOnClickListener { setupFinishButton() }
    }

    /**
     * Configura el selector de código de país para el número de teléfono, utilizando una librería externa.
     */
    private fun setupCountryCodePicker() {
        phoneNumberUtil = PhoneNumberUtil.getInstance()
        countryCodePicker = findViewById(R.id.ccp)
        countryCodePicker.setCountryForNameCode("MX")
        phoneNumberEditText = binding.TILPhoneNumber.editText as TextInputEditText
    }

    /**
     * Configura el menú desplegable para seleccionar el tipo de sangre.
     */
    private fun setupBloodTypeDropdown() {
        val bloodTypes = resources.getStringArray(R.array.bloodTypes).toList()
        val adapter = ArrayAdapter(this, com.hbb20.R.layout.support_simple_spinner_dropdown_item, bloodTypes)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Muestra un diálogo para seleccionar la fecha de nacimiento y actualiza el botón con la fecha seleccionada.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                binding.BDate.text = selectedDate
            },
            year,
            month,
            day
        )
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -18)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        datePickerDialog.show()
    }

    /**
     * Inicializa las variables `email`, `username`, `name` y `password` utilizando los valores
     * pasados a través de `intent` desde la actividad anterior.
     */
    private fun initializeExtras() {
        email = intent.getStringExtra("email")
        username = intent.getStringExtra("username")
        name = intent.getStringExtra("name")
        password = intent.getStringExtra("password")
    }

    /**
     * Configura el comportamiento del botón de retroceso para eliminar al usuario registrado
     * en caso de que se quiera cancelar el registro antes de completarlo.
     */
    private fun setupBackButton() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this@RegisterFinishActivity, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RegisterFinishActivity", "Usuario eliminado correctamente")
                val intent = Intent(this@RegisterFinishActivity, SessionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("RegisterFinishActivity", "Error al eliminar usuario: ${task.exception?.message}")
                Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Maneja la lógica para finalizar el registro, validando los datos del usuario
     * y enviando la información al ViewModel para completar el registro.
     */
    private fun setupFinishButton() {
        binding.BFinish.isEnabled = false
        val birthdate = binding.BDate.text.toString()
        val bloodType = binding.autoCompleteTextView.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.BFinish.isEnabled = true
        }, 2500)
        lifecycleScope.launch {
            val errorMessage =
                registerFinishViewModel.validate(birthdate, bloodType, phoneNumber)
            Log.d("RegisterFinishActivity", "errorMessage: $errorMessage")
            if (errorMessage != null) {
                Toast.makeText(this@RegisterFinishActivity, errorMessage, Toast.LENGTH_SHORT).show()
                when {
                    errorMessage.contains("tipo de sangre") -> {
                        binding.autoCompleteTextView.error = errorMessage
                    }
                    errorMessage.contains("número de teléfono") -> {
                        phoneNumberEditText.error = errorMessage
                    }
                }
            } else {
                if (registerFinishViewModel.authState.value is AuthState.VerificationSent) {
                    registerFinishViewModel.verifyEmailAndRegister(username!!, name!!, birthdate, bloodType, phoneNumber)
                } else {
                    registerFinishViewModel.registerUser(email!!, username!!, name!!, password!!, birthdate, bloodType, phoneNumber)
                }

            }
        }
    }

    /**
     * Sobrescribe el comportamiento del botón de retroceso del sistema para asegurarse de que
     * el usuario se elimine de Firebase si se cancela el registro a mitad de proceso.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RegisterFinishActivity", "Usuario eliminado correctamente")
                // Redirigir a la pantalla de sesión o inicio
                val intent = Intent(this, SessionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("RegisterFinishActivity", "Error al eliminar usuario: ${task.exception?.message}")
                Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}