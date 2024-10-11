package com.kotlin.sacalabici.framework.viewmodel.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginFinishViewModel : ViewModel() {

    // Lista de tipos de sangre válidos
    private val validBloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "No especificado")
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState
    private val userClient = UserClient()

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            registerUser(currentUser, name, birthdate, bloodType, phoneNumber)
        }
    }

    suspend fun validate(birthdate: String, bloodType: String, phoneNumber: String, name: String): String? {
        return withContext(Dispatchers.IO) {
            if (birthdate.isEmpty() || bloodType.isEmpty() || phoneNumber.isEmpty() || name.isEmpty()) {
                "Por favor, complete todos los campos"
            }
            else if (birthdate.isEmpty() || birthdate == "Año-Mes-Día") {
                "Por favor, ingrese una fecha válida"
            }
            else if (!isValidPhoneNumber(phoneNumber)) {
                "Por favor, ingrese un número de teléfono válido"
            }
            else if (bloodType.isNotEmpty() && !validBloodTypes.contains(bloodType)) {
                "Por favor, seleccione un tipo de sangre válido"
            }
            else if (name.length < 3) {
                "El nombre completo debe tener al menos 3 caracteres"
            }
            else if (name.length > 50) {
                "El nombre completo no puede tener más de 50 caracteres"
            }
            else if (!isValidName(name)) {
                "El nombre completo no puede contener números ni espacios a los extremos"
            }
            else {
                null
            }
        }
    }

    private fun isValidName(name: String): Boolean {// Check if the username is empty or contains only whitespace
        if (name.isBlank()) {
            return false
        }

        // Check if the username contains more than one consecutive space
        if (name.contains("\\s{2,}".toRegex())) {
            return false
        }// Check if the username starts or ends with a space
        if (name.startsWith(" ") || name.endsWith(" ")) {
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern= Pattern.compile("^\\d{10}\$") // Example for 10-digit numbers
        return pattern.matcher(phoneNumber).matches()
    }

    private fun registerUser(currentUser: FirebaseUser, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        viewModelScope.launch {
            userClient.registerUser(currentUser = currentUser, firebaseAuth = firebaseAuth, _authState = _authState, name = name, birthdate = birthdate, bloodType = bloodType, phoneNumber = phoneNumber)
        }
    }
}