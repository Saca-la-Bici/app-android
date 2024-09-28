package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import com.kotlin.sacalabici.framework.adapters.viewmodel.session.LoginFinishViewModel
import com.kotlin.sacalabici.framework.adapters.views.activities.MainActivity
import kotlinx.coroutines.launch
import java.util.Calendar

class LoginFinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginFinishBinding
    private val loginFinishViewModel: LoginFinishViewModel by viewModels()
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        // Inicializa el utilitario de PhoneNumber
        phoneNumberUtil = PhoneNumberUtil.getInstance()

        // Encuentra el CountryCodePicker en el layout
        countryCodePicker = findViewById(R.id.ccp)
        countryCodePicker.setCountryForNameCode("MX")
        phoneNumberEditText = binding.TILPhoneNumber.editText as TextInputEditText

        // Opciones de tipo de sangre
        val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

        // Configurar el adaptador para el AutoCompleteTextView
        val adapter = ArrayAdapter(this, com.hbb20.R.layout.support_simple_spinner_dropdown_item, bloodTypes)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)
        // Initialize ViewModel
        loginFinishViewModel.initialize(FirebaseAuth.getInstance())

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    FirebaseAuth.getInstance().signOut()
                }
            }
        })

        // Observe registration state
        loginFinishViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    // Registration successful
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish() // Optional: Finish RegisteerContinueActivity to prevent going back
                }

                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.IncompleteProfile -> {
                    Log.d("LoginFinishActivity", "Usuario incompleto")
                }
                is AuthState.CompleteProfile -> {
                    Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Unauthenticated -> {
                    val intent = Intent(this, SessionActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
                else -> {}
            }
        }

        binding.BDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.BBack.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@LoginFinishActivity, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.BFinish.setOnClickListener {
            val birthdate = binding.BDate.text.toString()
            val bloodType = binding.autoCompleteTextView.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val name = binding.TILName.editText?.text.toString()

            lifecycleScope.launch {
                val errorMessage =
                    loginFinishViewModel.validate(birthdate, bloodType, phoneNumber, name)
                Log.d("RegisterFinishActivity", "errorMessage: $errorMessage")
                if (errorMessage != null) {
                    Toast.makeText(this@LoginFinishActivity, errorMessage, Toast.LENGTH_SHORT)
                        .show()
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

    private fun initializeBinding() {
        binding = ActivityLoginFinishBinding.inflate(layoutInflater)
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
        maxDate.add(Calendar.YEAR, -13)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }
}
