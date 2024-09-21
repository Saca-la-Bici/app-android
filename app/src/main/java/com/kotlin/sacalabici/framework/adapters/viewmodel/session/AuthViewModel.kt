package com.kotlin.sacalabici.framework.adapters.viewmodel.session

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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.models.session.UserClient
import com.kotlin.sacalabici.utils.Constants
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    private val userClient = UserClient()
    val authState: LiveData<AuthState> get() = _authState
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager // Crear instancia de SessionRequirement

    fun initialize(firebaseAuthInstance: FirebaseAuth, googleOptions: GoogleSignInOptions, activity: AppCompatActivity) {
        firebaseAuth = firebaseAuthInstance
        googleSignInClient = GoogleSignIn.getClient(activity, googleOptions)
        callbackManager = CallbackManager.Factory.create()
    }

    // Getter para CallbackManager
    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }

    fun signInWithGoogle(activity: AppCompatActivity) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    fun signInWithFacebook(activity: AppCompatActivity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
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
                        registerUser(currentUser)
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
                    registerUser(currentUser)
                } else {
                    _authState.postValue(AuthState.Error("Usuario actual no disponible"))
                }
            } else {
                _authState.postValue(AuthState.Error("Autenticación fallida con Google"))
            }
        }
    }

// Autenticación por correo y contraseña
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
                        _authState.postValue(AuthState.Error("Error al iniciar sesión con correo y contraseña: ${task.exception?.message}"))
                    }
                }
        } else {
            _authState.postValue(AuthState.Error("Por favor, completa todos los campos"))
        }
    }


    fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.postValue(AuthState.Success(currentUser))
        }
    }

    private fun registerUser(currentUser: FirebaseUser) {
        viewModelScope.launch {
            userClient.registerUser(currentUser, firebaseAuth, _authState)
        }
    }
}
