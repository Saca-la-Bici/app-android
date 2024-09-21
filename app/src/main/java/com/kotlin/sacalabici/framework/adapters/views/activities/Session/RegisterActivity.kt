package com.kotlin.sacalabici.framework.adapters.views.activities.Session

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityRegisterUserBinding
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterUserBinding
    // private val viewModel: RegisterViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendario = Calendar.getInstance()

        val fecha = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val edad = Period.between(selectedDate, LocalDate.now()).years

            if (edad < 18) {
                // Show error message: "You must be at least 18 years old"
                Toast.makeText(this, "Debes ser mayor de 18 años", Toast.LENGTH_SHORT).show()
            } else {
                // Valid date, proceed with updating the date
                calendario.set(Calendar.YEAR, year)
                calendario.set(Calendar.MONTH, month)
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                actualizarFecha(calendario)
            }
        }

        initializeBinding()

        binding.BContinue.setOnClickListener {
            val email = binding.TILEmail.editText?.text.toString()
            val username = binding.TILUsername.editText?.text.toString()
            val birthday = binding.BDate.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.BContinue.isEnabled = true
            }, 5000)

            @RequiresApi(Build.VERSION_CODES.O)
            if (isValidEmail(email) && isValidUsername(username) && isValidBirthday(birthday)) {

                val intent = Intent(this, RegisterContinueActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("username", username)
                intent.putExtra("fechaNacimiento", birthday)
                startActivity(intent)
            } else {
                if (!isValidEmail(email)) {
                    binding.BContinue.isEnabled = false
                    binding.TILEmail.error = "Por favor ingresa un correo electrónico válido"
                    return@setOnClickListener
                }
                if (!isValidUsername(username)) {
                    binding.BContinue.isEnabled = false
                    binding.TILUsername.error = "Por favor ingresa un nombre de usuario válido"
                    return@setOnClickListener
                }
                if (!isValidBirthday(birthday)) {
                    binding.BContinue.isEnabled = false
                    Toast.makeText(this, "Por favor selecciona tu fecha de nacimiento", Toast.LENGTH_SHORT).show()
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
        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) return false
        if (username.matches(Regex("^[0-9]+$"))) return false
        return username.isNotEmpty() && username.length >= 3
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun actualizarFecha(calendario: Calendar) {
        val formatoFecha = "yyyy-MM-dd"
        val formatoSimple = SimpleDateFormat(formatoFecha, Locale.ENGLISH)
        binding.BDate.text = formatoSimple.format(calendario.time)
    }
}
