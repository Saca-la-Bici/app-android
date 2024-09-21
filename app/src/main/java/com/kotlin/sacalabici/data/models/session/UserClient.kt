package com.kotlin.sacalabici.data.models.session

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.domain.session.SessionRequirement
import kotlinx.coroutines.tasks.await

class UserClient {
    suspend fun registerUser(
        currentUser: FirebaseUser,
        firebaseAuth: FirebaseAuth,
        _authState: MutableLiveData<AuthState>,
        username: String? = null,
        fechaNacimiento: String? = null) {
        val idToken = getFirebaseIdToken(firebaseAuth)

        val user = User(
            username = username ?: currentUser.displayName ?: "",
            nombre = "",
            fechaNacimiento = fechaNacimiento ?: "",
            tipoSangre = "",
            correoElectronico = currentUser.email ?: "",
            numeroEmergencia = currentUser.phoneNumber ?: "",
            firebaseUID = currentUser.uid ?: ""
        )

        if (idToken != null) {
            val sessionRequirement = SessionRequirement(idToken)
            val result = sessionRequirement(user)

            if (result != null) {
                _authState.postValue(AuthState.Success(firebaseAuth.currentUser))
            } else {
                firebaseAuth.signOut()
                _authState.postValue(AuthState.Error("Error al iniciar sesión"))
            }
        } else {
            firebaseAuth.signOut()
            _authState.postValue(AuthState.Error("Error al obtener el token de Firebase"))
        }
    }

    private suspend fun getFirebaseIdToken(firebaseAuth: FirebaseAuth): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
}