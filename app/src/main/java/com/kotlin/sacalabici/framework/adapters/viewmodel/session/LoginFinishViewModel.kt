package com.kotlin.sacalabici.framework.adapters.viewmodel.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginFinishViewModel : ViewModel() {

    // Lista de tipos de sangre válidos
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    private val validBloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState

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
            else if (birthdate.isEmpty()) {
                "Por favor, ingrese una fecha válida"
            }
            else if (!isValidPhoneNumber(phoneNumber)) {
                "Por favor, ingrese un número de teléfono válido"
            }
            else if (bloodType.isNotEmpty() && !validBloodTypes.contains(bloodType)) {
                "Por favor, seleccione un tipo de sangre válido"
            }
            else {
                null
            }
        }
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