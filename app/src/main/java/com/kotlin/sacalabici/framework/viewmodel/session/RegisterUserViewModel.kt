package com.kotlin.sacalabici.framework.adapters.viewmodel.session
import android.util.Log
import androidx.lifecycle.ViewModel
import com.kotlin.sacalabici.data.models.session.GetUsernameObject
import com.kotlin.sacalabici.domain.session.RegisterUserRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterUserViewModel : ViewModel() {
    private val registerUserRequirement = RegisterUserRequirement()


    suspend fun validate(email: String, username: String, name: String): String? {
        return withContext(Dispatchers.IO) {
            if (email.isEmpty() || username.isEmpty() || name.isEmpty()) {
                "Por favor, complete todos los campos"
            }
            else if(!isValidEmail(email)){
                "Por favor, ingrese un correo electrónico válido"
            }
            else if (username.length < 3) {
                "El nombre de usuario debe tener al menos 3 caracteres"
            }
            else if (name.length < 3) {
                "El nombre completo debe tener al menos 3 caracteres"
            }
            else if (!isValidUsername(username)) {
                "El nombre de usuario solo puede contener letras y números"
            }
            else if (!checkUsernameUniqueness(username)) {
                "El nombre de usuario ya está en uso"
            } else {
                null // No errors
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return if (email.isBlank()) {false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    private suspend fun isValidUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            val usernameRegex = Regex("^[a-zA-Z0-9]+$")
            usernameRegex.matches(username) && username.length in 3..20
        }
    }

    private suspend fun checkUsernameUniqueness(username: String): Boolean {
        val getUsernameObject = registerUserRequirement(username)

        if (getUsernameObject != null && getUsernameObject.usernameExistente) {
            Log.d("Username", "Username exists: ${getUsernameObject.usernameExistente}")
            return false
        }

        Log.d("Username", "Username does not exist or response is null")
        return true
    }
}