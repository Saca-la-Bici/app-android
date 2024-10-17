/**
 * File: LoginFinishActivity.kt
 * Description: Esta actividad permite a los usuarios completar su perfil después de iniciar sesión.
 *              Los usuarios deben proporcionar información adicional como fecha de nacimiento, tipo
 *              de sangre, número de teléfono y nombre. La información se valida antes de registrarla.
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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.hbb20.CountryCodePicker
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.databinding.ActivityLoginFinishBinding
import com.kotlin.sacalabici.framework.viewmodel.session.LoginFinishViewModel
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import kotlinx.coroutines.launch
import java.util.Calendar
@Suppress("NAME_SHADOWING")

/**
 * Actividad para completar el perfil del usuario. Los usuarios proporcionan información adicional
 * como fecha de nacimiento, tipo de sangre, número de teléfono y nombre. Incluye validaciones de datos
 * y la opción de regresar a la pantalla de sesión si se desea cerrar sesión.
 */
class LoginFinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginFinishBinding
    private val loginFinishViewModel: LoginFinishViewModel by viewModels()
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeViewModel()
        observeAuthState()
        setupCountryCodePicker()
        setupBloodTypeDropdown()
        setupButtonListeners()
    }

    /**
     * Observa los cambios en el estado de autenticación y maneja las acciones correspondientes
     * dependiendo del estado, como redirigir a la pantalla principal o mostrar mensajes de error.
     */
    private fun observeAuthState() {
        loginFinishViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    navigateTo(MainActivity::class.java)
                }
                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.IncompleteProfile -> {
                    Log.d("LoginFinishActivity", "Usuario incompleto")
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

    private fun initializeViewModel() {
        loginFinishViewModel.initialize(FirebaseAuth.getInstance())
    }

    private fun initializeBinding() {
        binding = ActivityLoginFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Configura los listeners para los botones de la interfaz de usuario, incluyendo el botón de
     * selección de fecha de nacimiento y el botón de finalización del registro. Realiza validaciones
     * sobre los datos ingresados antes de registrar al usuario.
     */
    private fun setupButtonListeners() {
        binding.BDate.setOnClickListener { showDatePickerDialog() }
        binding.BBack.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateTo(SessionActivity::class.java)
        }
        binding.BFinish.setOnClickListener {
            val birthdate = binding.BDate.text.toString()
            val bloodType = binding.autoCompleteTextView.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val name = binding.TILName.editText?.text.toString()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.BFinish.isEnabled = true
            }, 5000)
            lifecycleScope.launch {
                val errorMessage =
                    loginFinishViewModel.validate(birthdate, bloodType, phoneNumber, name)
                Log.d("RegisterFinishActivity", "errorMessage: $errorMessage")
                if (errorMessage != null) {
                    binding.BFinish.isEnabled = false
                    Toast.makeText(this@LoginFinishActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    when {
                        errorMessage.contains("tipo de sangre") -> {
                            binding.autoCompleteTextView.error = errorMessage
                        }
                        errorMessage.contains("número de teléfono") -> {
                            phoneNumberEditText.error = errorMessage
                        }
                        errorMessage.contains("nombre") -> {
                            binding.TILName.error = errorMessage
                        }
                    }
                } else {
                    loginFinishViewModel.registerUser(
                        name,
                        birthdate,
                        bloodType,
                        phoneNumber
                    )
                }
            }
        }
    }

    /**
     * Configura el selector de código de país para el número de teléfono del usuario utilizando
     * la biblioteca `PhoneNumberUtil`.
     */
    private fun setupCountryCodePicker() {
        phoneNumberUtil = PhoneNumberUtil.getInstance()
        countryCodePicker = findViewById(R.id.ccp)
        countryCodePicker.setCountryForNameCode("MX")
        phoneNumberEditText = binding.TILPhoneNumber.editText as TextInputEditText
    }

    /**
     * Configura el dropdown de tipos de sangre en el formulario de finalización de perfil.
     */
    private fun setupBloodTypeDropdown() {
        val bloodTypes = resources.getStringArray(R.array.bloodTypes).toList()
        val adapter = ArrayAdapter(this, com.hbb20.R.layout.support_simple_spinner_dropdown_item, bloodTypes)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)
    }

    /**
     * Muestra un cuadro de diálogo de selección de fecha para que el usuario ingrese su fecha de nacimiento.
     * Solo permite fechas que indiquen que el usuario tiene más de 18 años.
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
}