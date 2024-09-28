package com.kotlin.sacalabici.framework.adapters.viewmodel.session

import androidx.lifecycle.ViewModel

class RegisterContinueViewModel: ViewModel() {

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