package com.kotlin.sacalabici.framework.views.activities.session

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.google.android.material.textfield.TextInputEditText
import com.kotlin.sacalabici.framework.views.activities.session.LoginActivity
class RecoverPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var delayTime = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
        auth = FirebaseAuth.getInstance()
        val backButton = findViewById<ShapeableImageView>(R.id.BBack)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val forgotPasswordTextView = findViewById<TextView>(R.id.TVForgotPassword)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val recoverButton = findViewById<Button>(R.id.BSession)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        recoverButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                enviarCorreoRecuperacion(email, recoverButton)
            } else {
                Toast.makeText(this, "Por favor, ingresa un correo válido", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enviarCorreoRecuperacion(email: String, recoverButton: Button) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo enviado para recuperación de contraseña", Toast.LENGTH_SHORT).show()
                    // Cambiar el texto del botón
                    recoverButton.text = "Correo enviado"
                    recoverButton.isEnabled = false
                    bloquearBotonTemporalmente(recoverButton)
                } else {
                    Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun bloquearBotonTemporalmente(recoverButton: Button) {
        Handler(Looper.getMainLooper()).postDelayed({
            recoverButton.isEnabled = true
            recoverButton.text = "Enviar correo"
            delayTime *= 2
        }, delayTime)
    }
}