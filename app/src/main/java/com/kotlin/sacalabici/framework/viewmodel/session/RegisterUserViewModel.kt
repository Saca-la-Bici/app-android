package com.kotlin.sacalabici.framework.viewmodel.session

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kotlin.sacalabici.domain.session.RegisterUserRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ViewModel para la gestión del registro de usuarios en la aplicación "Saca la Bici".
 * Este ViewModel se encarga de validar la información ingresada por el usuario al registrarse.
 */
class RegisterUserViewModel : ViewModel() {
    private val registerUserRequirement = RegisterUserRequirement()

    /**
     * Valida los campos de entrada del registro de usuario.
     *
     * @param email El correo electrónico ingresado por el usuario.
     * @param username El nombre de usuario ingresado por el usuario.
     * @param name El nombre completo ingresado por el usuario.
     * @return Un mensaje de error si hay un problema de validación, o null si no hay errores.
     */
    suspend fun validate(email: String, username: String, name: String): String? {
        return withContext(Dispatchers.IO) {
            if (email.isEmpty() || username.isEmpty() || name.isEmpty()) {
                "Por favor, complete todos los campos"
            } else if (!isValidEmail(email)) {
                "Por favor, ingrese un correo electrónico válido"
            } else if (username.length < 3) {
                "El nombre de usuario debe tener al menos 3 caracteres"
            } else if (username.length > 20) {
                "El nombre de usuario no puede tener más de 20 caracteres"
            } else if (name.length < 3) {
                "El nombre completo debe tener al menos 3 caracteres"
            } else if (name.length > 50) {
                "El nombre completo no puede tener más de 50 caracteres"
            } else if (!isValidUsername(username)) {
                "El nombre de usuario solo puede contener letras y números"
            } else if (!isValidName(name)) {
                "El nombre completo no puede contener números ni espacios a los extremos"
            } else if (!checkUsernameUniqueness(username)) {
                "El nombre de usuario ya está en uso"
            } else {
                null // No hay errores
            }
        }
    }

    /**
     * Valida si el correo electrónico ingresado es correcto.
     *
     * @param email El correo electrónico a validar.
     * @return true si el correo electrónico es válido, false en caso contrario.
     */
    private fun isValidEmail(email: String): Boolean {
        return if (email.isBlank()) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    /**
     * Valida el nombre de usuario ingresado, comprobando que contenga solo letras y números.
     *
     * @param username El nombre de usuario a validar.
     * @return true si el nombre de usuario es válido, false en caso contrario.
     */
    private suspend fun isValidUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            val usernameRegex = Regex("^[a-zA-Z0-9]+$")
            usernameRegex.matches(username) && username.length in 3..20
        }
    }

    /**
     * Valida si el nombre completo ingresado es correcto, asegurándose de que no contenga números ni espacios al inicio o al final.
     *
     * @param name El nombre completo a validar.
     * @return true si el nombre completo es válido, false en caso contrario.
     */
    private fun isValidName(name: String): Boolean {
        // Comprobar si el nombre está vacío o contiene solo espacios en blanco
        if (name.isBlank()) {
            return false
        }

        // Comprobar si el nombre contiene más de un espacio consecutivo
        if (name.contains("\\s{2,}".toRegex())) {
            return false
        }

        // Comprobar si el nombre comienza o termina con un espacio
        if (name.startsWith(" ") || name.endsWith(" ")) {
            return false
        }

        return true
    }

    /**
     * Comprueba la unicidad del nombre de usuario consultando si ya existe en el sistema.
     *
     * @param username El nombre de usuario a comprobar.
     * @return true si el nombre de usuario es único, false si ya existe.
     */
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
