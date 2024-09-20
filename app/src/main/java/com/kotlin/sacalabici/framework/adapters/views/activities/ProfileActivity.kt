package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityProfileBinding
import com.kotlin.sacalabici.framework.adapters.views.fragments.EventFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.GlobalFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.MedalsFragment

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()

        replaceFragment(EventFragment())

        val btnEventos = binding.btnEventos
        val btnAsistencia = binding.btnAsistencia
        val btnGlobal = binding.btnGlobal

        btnEventos.setOnClickListener {
            highlightCurrentActivity("Eventos", btnEventos, btnAsistencia, btnGlobal)
        }
        btnAsistencia.setOnClickListener {
            highlightCurrentActivity("Asistencia", btnEventos, btnAsistencia, btnGlobal)
        }
        btnGlobal.setOnClickListener {
            highlightCurrentActivity("Global", btnEventos, btnAsistencia, btnGlobal)
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
        btnGlobal: ImageButton
    ) {
        // Reiniciar estilos de los botones
        resetButtonStyles(btnEventos, btnAsistencia, btnGlobal)

        // Cambiar estilo del botón y actualizar la vista
        when (currentActivity) {
            "Eventos" -> {
                btnEventos.setImageResource(R.drawable.ic_event_selected)
                replaceFragment(EventFragment())
            }
            "Asistencia" -> {
                btnAsistencia.setImageResource(R.drawable.ic_check_selected)
                replaceFragment(MedalsFragment())
            }
            "Global" -> {
                btnGlobal.setImageResource(R.drawable.ic_global_selected)
                replaceFragment(GlobalFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.vFragment, fragment)
            .commit()
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
