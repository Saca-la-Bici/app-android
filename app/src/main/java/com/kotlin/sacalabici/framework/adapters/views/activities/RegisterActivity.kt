package com.kotlin.sacalabici.framework.adapters.views.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityRegisterUserBinding
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterUserBinding
    // private val viewModel: RegisterViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendario = Calendar.getInstance()

        val fecha = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            actualizarFecha(calendario)
        }

        initializeBinding()

        binding.BContinue.setOnClickListener {
            val email = binding.TILEmail.editText?.text.toString()
            val username = binding.TILUsername.editText?.text.toString()
            val birthday = binding.BDate.text.toString()

            @RequiresApi(Build.VERSION_CODES.O)
            if (isValidEmail(email) && isValidUsername(username) && isValidBirthday(birthday)) {
                val edad = calcularEdad(birthday)

                val intent = Intent(this, RegisterContinueActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("username", username)
                intent.putExtra("edad", edad)
                startActivity(intent)
            } else {
                if (!isValidEmail(email)) {
                    binding.TILEmail.error = "Invalid email"
                }
                if (!isValidUsername(username)) {
                    binding.TILUsername.error = "Invalid username"
                }
                if (!isValidBirthday(birthday)) {
                    Toast.makeText(this, "Please select your birthday", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.BBack.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }

        binding.BDate.setOnClickListener {
            DatePickerDialog(
                this,
                fecha,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun isValidBirthday(birthday: String): Boolean {
        return birthday != getString(R.string.TDate) // Assuming "Select Date" is the default text
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        return username.isNotEmpty() && username.length >= 3
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularEdad(birthday: String): Int {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaNacimiento = LocalDate.parse(birthday, formatter)
        val fechaActual = LocalDate.now()
        val periodo = Period.between(fechaNacimiento, fechaActual)
        return periodo.years
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun actualizarFecha(calendario: Calendar) {
        val formatoFecha = "dd/MM/yyyy"
        val formatoSimple = SimpleDateFormat(formatoFecha, Locale.ENGLISH)
        binding.BDate.text = formatoSimple.format(calendario.time)
    }
}
