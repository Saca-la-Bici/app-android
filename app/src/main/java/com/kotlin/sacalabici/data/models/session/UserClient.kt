package com.kotlin.sacalabici.data.models.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kotlin.sacalabici.data.models.user.User
import com.kotlin.sacalabici.domain.session.SessionRequirement
import kotlinx.coroutines.tasks.await

class UserClient {
    suspend fun registerUser(
        currentUser: FirebaseUser,
        firebaseAuth: FirebaseAuth,
        _authState: MutableLiveData<AuthState>,
        username: String? = null,
        name: String? = null,
        birthdate: String? = null,
        bloodType: String? = null,
        phoneNumber: String? = null,
    ) {
        val idToken = getFirebaseIdToken(firebaseAuth)

        val user = User(
            username = username ?: currentUser.displayName ?: "usuario",
            nombre = name ?: "",
            fechaNacimiento = birthdate ?: "",
            tipoSangre = bloodType ?: "",
            correoElectronico = currentUser.email ?: "",
            numeroEmergencia = phoneNumber ?: "",
            firebaseUID = currentUser.uid ?: ""
        )

        Log.d("UserClient", "User: $user")

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

    suspend fun getUser(firebaseAuth: FirebaseAuth): PerfilCompletoObject? {
        val idToken = getFirebaseIdToken(firebaseAuth)
        if (idToken != null) {
            val sessionRequirement = SessionRequirement(idToken)
            return sessionRequirement.getUser()
        }
        return null
    }

    private suspend fun getFirebaseIdToken(firebaseAuth: FirebaseAuth): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
}