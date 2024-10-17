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

/**
 * ViewModel para completar el registro de usuario en la aplicación "Saca la Bici".
 * Este ViewModel se encarga de registrar a los usuarios, enviar correos de verificación y
 * gestionar el estado de autenticación.
 */
class RegisterFinishViewModel : ViewModel() {

    // Lista de tipos de sangre válidos
    private val validBloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "No especificado")
    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState

    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * Inicializa la instancia de FirebaseAuth.
     *
     * @param firebaseAuthInstance Instancia de FirebaseAuth que se utilizará para autenticación.
     */
    fun initialize(firebaseAuthInstance: FirebaseAuth) {
        firebaseAuth = firebaseAuthInstance
    }

    /**
     * Registra a un usuario en Firebase si el correo electrónico no está en uso.
     *
     * @param email El correo electrónico del usuario.
     * @param username El nombre de usuario del usuario.
     * @param name El nombre completo del usuario.
     * @param password La contraseña del usuario.
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     */
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

    /**
     * Crea un nuevo usuario en Firebase utilizando el correo electrónico y la contraseña proporcionados.
     *
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @param username El nombre de usuario del usuario.
     * @param name El nombre completo del usuario.
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     */
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

    /**
     * Envía un correo electrónico de verificación al usuario registrado.
     *
     * @param currentUser El usuario actual que fue creado en Firebase.
     * @param username El nombre de usuario del usuario.
     * @param name El nombre completo del usuario.
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     */
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

    /**
     * Verifica si el correo del usuario ha sido verificado y, si es así, registra al usuario en la base de datos.
     *
     * @param username El nombre de usuario del usuario.
     * @param name El nombre completo del usuario.
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     */
    fun verifyEmailAndRegister(username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.reload() // Recargar el usuario para actualizar el estado de verificación
        if (currentUser != null && currentUser.isEmailVerified) {
            registerUserInDatabase(currentUser, username, name, birthdate, bloodType, phoneNumber)
        } else {
            _authState.postValue(AuthState.VerificationSent("Verifica tu correo o vuelve a intentarlo."))
        }
    }

    /**
     * Registra al usuario en la base de datos una vez que el correo ha sido verificado.
     *
     * @param currentUser El usuario actual que ha verificado su correo.
     * @param username El nombre de usuario del usuario.
     * @param name El nombre completo del usuario.
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     */
    private fun registerUserInDatabase(currentUser: FirebaseUser, username: String, name: String, birthdate: String, bloodType: String, phoneNumber: String) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState, username, name, birthdate, bloodType, phoneNumber)
        }
    }

    /**
     * Maneja errores que ocurren durante el registro del usuario.
     *
     * @param exception La excepción lanzada durante el registro.
     */
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

    /**
     * Valida la información ingresada por el usuario antes de registrar.
     *
     * @param birthdate La fecha de nacimiento del usuario.
     * @param bloodType El tipo de sangre del usuario.
     * @param phoneNumber El número de teléfono del usuario.
     * @return Un mensaje de error si hay un problema de validación, o null si no hay errores.
     */
    suspend fun validate(birthdate: String, bloodType: String, phoneNumber: String): String? {
        return withContext(Dispatchers.IO) {
            if (birthdate.isEmpty() || bloodType.isEmpty() || phoneNumber.isEmpty()) {
                "Por favor, complete todos los campos"
            } else if (birthdate.isEmpty() || birthdate == "Año-Mes-Día") {
                "Por favor, ingrese una fecha válida"
            } else if (!isValidPhoneNumber(phoneNumber)) {
                "Por favor, ingrese un número de teléfono válido"
            } else if (bloodType.isNotEmpty() && !validBloodTypes.contains(bloodType)) {
                "Por favor, seleccione un tipo de sangre válido"
            } else {
                null
            }
        }
    }

    /**
     * Verifica si el número de teléfono ingresado es válido.
     *
     * @param phoneNumber El número de teléfono a validar.
     * @return true si el número de teléfono es válido, false en caso contrario.
     */
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Pattern.compile("^\\d{10}\$") // Ejemplo para números de 10 dígitos
        return pattern.matcher(phoneNumber).matches()
    }
}
