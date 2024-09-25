package com.kotlin.sacalabici.data.network.announcements

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class FirebaseTokenManager(private val firebaseAuth: FirebaseAuth) {
    private var token: String? = null

    fun getIdToken() {
        firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                this.token = task.result?.token
                Log.d("token", this.token!!)
            } else {
                Log.e("FirebaseTokenManager", "Failed to get ID token", task.exception)
            }
        }
    }

    fun returnToken(): String? {
        return this.token
    }

    fun refreshIdToken(callback: (String?) -> Unit) {
        firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                this.token = task.result?.token
                callback(task.result?.token)
            } else {
                callback(null)
            }
        }
    }
}