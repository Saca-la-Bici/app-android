package com.kotlin.sacalabici.framework.viewmodel.session

import androidx.lifecycle.ViewModel

/**
 * ViewModel para la continuación del registro de usuario en la aplicación "Saca la Bici".
 * Este ViewModel se encarga de validar las contraseñas proporcionadas por el usuario.
 */
class RegisterContinueViewModel: ViewModel() {

    /**
     * Valida si las contraseñas proporcionadas son válidas y cumplen con ciertos criterios.
     *
     * @param password La contraseña ingresada por el usuario.
     * @param confirmPassword La confirmación de la contraseña ingresada por el usuario.
     * @return Un mensaje de error si las contraseñas no son válidas o coinciden, o null si son válidas.
     */
    fun arePasswordsValid(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) return "Las contraseñas no coinciden"
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!password.contains(Regex("[0-9]"))) return "La contraseña debe contener al menos un número"
        if (!password.contains(Regex("[A-Z]"))) return "La contraseña debe contener al menos una letra mayúscula"
        if (!password.contains(Regex("[a-z]"))) return "La contraseña debe contener al menos una letra minúscula"
        if (!password.contains(Regex("[^a-zA-Z0-9\\s]"))) return "La contraseña debe contener al menos un carácter especial"
        return null // No errors
    }
}
