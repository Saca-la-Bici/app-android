package com.kotlin.sacalabici.framework.viewmodel.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class RegisterFinishViewModel : ViewModel() {

    // Lista de tipos de sangre válidos
    private val validBloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "No especificado")
    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState

    private lateinit var firebaseAuth: FirebaseAuth

    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    fun registerUser(email: String, username: String, name: String, password: String, birthdate: String, bloodType: String, phoneNumber: String) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        // El correo no está registrado, procede con la creación del usuario
                        createFirebaseUser(email, password, username, name, birthdate, bloodType, phoneNumber)
                    } else {
                        // El correo ya está registrado
                        _authState.postValue(AuthState.Error("El correo electrónico ya está en uso"))
                    }
                } else {
                    _authState.postValue(AuthState.Error("Error al verificar el correo electrónico: ${task.exception?.localizedMessage}"))
                }
            }
    }

    private fun createFirebaseUser(email: String, password: String, username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        sendVerificationEmail(currentUser, username, name, birthdate, bloodType, phoneNumber)
                    }
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }


    private fun sendVerificationEmail(currentUser: FirebaseUser, username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        currentUser.sendEmailVerification()
            .addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    _authState.postValue(AuthState.VerificationSent("Se ha enviado un correo de verificación"))
                } else {
                    _authState.postValue(AuthState.Error("Error al enviar el correo de verificación: ${verificationTask.exception?.localizedMessage}"))
                }
            }
    }

    // Método para verificar el correo y registrar al usuario en la base de datos
    fun verifyEmailAndRegister(username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.reload() // Recargar el usuario para actualizar el estado de verificación
        if (currentUser != null && currentUser.isEmailVerified) {
            registerUserInDatabase(currentUser, username, name, birthdate, bloodType, phoneNumber)
        } else {
            _authState.postValue(AuthState.VerificationSent("Verifica tu correo o vuelve a intentarlo."))
        }
    }

    private fun registerUserInDatabase(currentUser: FirebaseUser, username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState, username, name, birthdate, bloodType, phoneNumber)
        }
    }

    private fun handleRegistrationError(exception: Exception?) {
        try {
            throw exception ?: Exception("Error desconocido")
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                    _authState.postValue(AuthState.Error("El correo electrónico ya está en uso"))
                }
                else -> {
                    _authState.postValue(AuthState.Error("Hubo un error al registrar el usuario: ${e.localizedMessage}"))
                }
            }
        }
    }

    suspend fun validate(birthdate: String, bloodType: String, phoneNumber: String): String? {
        return withContext(Dispatchers.IO) {
            if (birthdate.isEmpty() || bloodType.isEmpty() || phoneNumber.isEmpty()) {
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
            else {
                null
            }
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Pattern.compile("^\\d{10}\$") // Example for 10-digit numbers
        return pattern.matcher(phoneNumber).matches()
    }
}
