/**
 * File: RecoverPasswordActivity.kt
 * Description: Actividad que permite a los usuarios recuperar su contraseña enviando un correo de restablecimiento
 *              utilizando Firebase Authentication. Incluye validaciones de correo y temporizadores para evitar
 *              múltiples envíos consecutivos de correos.
 * Date: 16/10/2024
 * Changes:
 */

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

/**
 * Actividad que permite a los usuarios recuperar sus contraseñas mediante el envío de un correo electrónico
 * de recuperación de contraseña utilizando Firebase Authentication.
 */
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

    /**
     * Envía un correo de recuperación de contraseña al usuario a través de Firebase Authentication.
     * Deshabilita temporalmente el botón para evitar múltiples envíos y actualiza el texto del botón.
     * @param email Correo electrónico al que se enviará el correo de recuperación.
     * @param recoverButton Botón de recuperación que se actualizará y deshabilitará temporalmente.
     */
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

    /**
     * Deshabilita el botón de recuperación temporalmente y lo vuelve a habilitar después de un retraso,
     * aumentando el tiempo de espera para evitar múltiples envíos consecutivos.
     * @param recoverButton Botón que será deshabilitado temporalmente.
     */
    private fun bloquearBotonTemporalmente(recoverButton: Button) {
        Handler(Looper.getMainLooper()).postDelayed({
            recoverButton.isEnabled = true
            recoverButton.text = "Enviar correo"
            delayTime *= 2
        }, delayTime)
    }
}