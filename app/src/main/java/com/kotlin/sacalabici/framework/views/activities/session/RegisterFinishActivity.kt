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
import kotlinx.coroutines.launch
import java.util.Calendar

@Suppress("NAME_SHADOWING")
class RegisterFinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserFinishBinding
    private val registerFinishViewModel: RegisterFinishViewModel by viewModels()
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("username")
        val name = intent.getStringExtra("name")
        val password = intent.getStringExtra("password")

        // Inicializa el utilitario de PhoneNumber
        phoneNumberUtil = PhoneNumberUtil.getInstance()

        // Encuentra el CountryCodePicker en el layout
        countryCodePicker = findViewById(R.id.ccp)
        countryCodePicker.setCountryForNameCode("MX")
        phoneNumberEditText = binding.TILPhoneNumber.editText as TextInputEditText

        // Opciones de tipo de sangre
        val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

        // Configurar el adaptador para el AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodTypes)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)
        // Initialize ViewModel
        registerFinishViewModel.initialize(FirebaseAuth.getInstance())

        // Observe registration state
        registerFinishViewModel.authState.observe(this) { authState ->
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
                    Toast.makeText(this, "Llena tu perfil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginFinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthState.CompleteProfile -> {
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Unauthenticated -> {
                    Log.d("LoginFinishActivity", "Usuario no autenticado")
                }

                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }

        binding.BDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.BBack.setOnClickListener {
            val intent = Intent(this@RegisterFinishActivity, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.BFinish.setOnClickListener {
            val birthdate = binding.BDate.text.toString()
            val bloodType = binding.autoCompleteTextView.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.BFinish.isEnabled = true
            }, 5000)


            lifecycleScope.launch {
                val errorMessage =
                    registerFinishViewModel.validate(birthdate, bloodType, phoneNumber)
                Log.d("RegisterFinishActivity", "errorMessage: $errorMessage")
                if (errorMessage != null) {
                    binding.BFinish.isEnabled = false
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
                    registerFinishViewModel.registerUser(
                        email!!,
                        username!!,
                        name!!,
                        password!!,
                        birthdate,
                        bloodType,
                        phoneNumber
                    )
                }
            }
        }
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

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
