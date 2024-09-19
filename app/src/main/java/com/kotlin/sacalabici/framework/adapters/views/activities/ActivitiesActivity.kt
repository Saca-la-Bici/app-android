package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class ActivitiesActivity: BaseActivity() {

    private lateinit var binding: AcivityActivitiesBinding
    private lateinit var firebaseAuth: FirebaseAuth

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        initializeBinding()

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            // Usuario no está autenticado, redirige a SessionActivity
            startActivity(Intent(this, SessionActivity::class.java))
            finish() // Opcional: Termina la actividad actual para que el usuario no pueda volver a ella con el botón "Atrás"
        } else {
            // Usuario autenticado, continúa con la lógica de ActivitiesActivity
            setupNavbar()
        }
    }

    private fun initializeBinding(){
        binding = AcivityActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}