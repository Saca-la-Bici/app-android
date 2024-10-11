@file:Suppress("DEPRECATION")

package com.kotlin.sacalabici.framework.viewmodel.session

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import com.kotlin.sacalabici.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState
    private val userClient = UserClient()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager


    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    fun initialize(firebaseAuthInstance: FirebaseAuth, googleOptions: GoogleSignInOptions, activity: AppCompatActivity) {
        firebaseAuth = firebaseAuthInstance
        googleSignInClient = GoogleSignIn.getClient(activity, googleOptions)
        callbackManager = CallbackManager.Factory.create()

        initializeAuthStateListener()
    }

    private fun initializeAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                checkUserProfile()
            }
        }
    }

    // Getter para CallbackManager
    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }

    // Agregar el listener de estado de autenticación
    fun startAuthStateListener() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun checkUserProfile() {
        viewModelScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userClient.getUser(firebaseAuth)
            }

            if (currentUser?.perfilRegistrado == false) {
                _authState.postValue(AuthState.IncompleteProfile)
            } else {
                Log.d("AuthViewModel", currentUser?.perfilRegistrado.toString())
                _authState.postValue(AuthState.CompleteProfile)
            }
        }
    }

    // Inicio de sesión con Google
    fun signInWithGoogle(activity: AppCompatActivity) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    // Inicio de sesión con Facebook
    fun signInWithFacebook(activity: AppCompatActivity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                _authState.postValue(AuthState.Cancel)
            }

            override fun onError(error: FacebookException) {
                _authState.postValue(AuthState.Error("Error al iniciar sesión con Facebook"))
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        checkUserProfile()
                    } else {
                        _authState.postValue(AuthState.Error("Usuario actual no disponible"))
                    }
                } else {
                    _authState.postValue(AuthState.Error("Autenticación fallida con Facebook"))
                }
            }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            _authState.postValue(AuthState.Error("Error al iniciar sesión con Google"))
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    checkUserProfile()
                } else {
                    _authState.postValue(AuthState.Error("Usuario actual no disponible"))
                }
            } else {
                _authState.postValue(AuthState.Error("Autenticación fallida o cancelada con Google"))
            }
        }
    }

    // Autenticación por correoy contraseña
    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            registerUser(currentUser)
                        } else {
                            _authState.postValue(AuthState.Error("Usuario actual no disponible"))
                        }
                    } else {
                        val errorMessage = when (val exception = task.exception) {
                            is FirebaseAuthInvalidUserException -> "La cuenta de usuario no existe o ha sido deshabilitada."
                            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
                            else -> exception?.message ?: "Error al iniciar sesión con correo y contraseña"
                        }
                        _authState.postValue(AuthState.Error(errorMessage))
                    }
                }
        } else {
            _authState.postValue(AuthState.Error("Por favor, ingrese un correo electrónico y una contraseña"))
        }
    }


    private fun registerUser(currentUser: FirebaseUser) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState)
        }
    }
}