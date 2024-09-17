package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.databinding.ActivityProfileBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()

        val btnEventos = binding.btnEventos
        val btnAsistencia = binding.btnAsistencia
        val btnGlobal = binding.btnGlobal
        val vSection = binding.vSection

        btnEventos.setOnClickListener {
            highlightCurrentActivity("Eventos", btnEventos, btnAsistencia, btnGlobal, vSection)
        }
        btnAsistencia.setOnClickListener {
            highlightCurrentActivity("Asistencia", btnEventos, btnAsistencia, btnGlobal, vSection)
        }
        btnGlobal.setOnClickListener {
            highlightCurrentActivity("Global", btnEventos, btnAsistencia, btnGlobal, vSection)
        }

        setupNavbar()
        setupConfiguracionButton()
        setupEditarButton()
    }

    private fun initializeBinding() {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Método para inicializar el listener del botón de configuración
    private fun setupConfiguracionButton() {
        val btnConfiguration = findViewById<ImageButton>(R.id.btn_configuration)
        btnConfiguration.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    // Método para inicializar el listener del botón de configuración
    private fun setupEditarButton() {
        val btnEditProfile = findViewById<ImageButton>(R.id.btn_edit_profile)
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


    private fun highlightCurrentActivity(
        currentActivity: String,
        btnEventos: ImageButton,
        btnAsistencia: ImageButton,
        btnGlobal: ImageButton,
        vSection: View
    ) {
        // Reiniciar estilos de los botones y la vista
        resetButtonStyles(btnEventos, btnAsistencia, btnGlobal)

        // Cambiar estilo del botón y actualizar la vista
        when (currentActivity) {
            "Eventos" -> {
                btnEventos.setImageResource(R.drawable.ic_event_selected)
                vSection.setBackgroundColor(Color.LTGRAY) // Cambiar color de la vista al seleccionar
            }
            "Asistencia" -> {
                btnAsistencia.setImageResource(R.drawable.ic_check_selected)
                vSection.setBackgroundColor(Color.DKGRAY) // Cambiar a otro color o contenido
            }
            "Global" -> {
                btnGlobal.setImageResource(R.drawable.ic_global_selected)
                vSection.setBackgroundColor(Color.CYAN) // Otro color o contenido
            }
        }
    }

    private fun resetButtonStyles(
        btnEventos: ImageButton,
        btnAsistencia: ImageButton,
        btnGlobal: ImageButton
    ) {
        // Restablecer iconos y fondos a su estado normal
        btnEventos.setImageResource(R.drawable.ic_event)
        btnAsistencia.setImageResource(R.drawable.ic_check)
        btnGlobal.setImageResource(R.drawable.ic_global)
    }

}
