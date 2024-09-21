package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityMainBinding
import com.kotlin.sacalabici.framework.adapters.views.activities.Session.SessionActivity
import com.kotlin.sacalabici.framework.adapters.views.fragments.ActivitiesFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.AnnouncementsFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.MapFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.ProfileFragment
import com.kotlin.sacalabici.utils.Constants


class MainActivity: AppCompatActivity() {
    private lateinit var currentFragment: Fragment

    private var currentMenuOption:String?= null

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        initializeObservers()
        initializeListeners()
        exchangeCurrentFragment(ActivitiesFragment(), Constants.MENU_ACTIVITIES)

        firebaseAuth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext)

        if (firebaseAuth.currentUser == null) {
            // Usuario no está autenticado, redirige a SessionActivity
            startActivity(Intent(this, SessionActivity::class.java))
            finish() // Opcional: Termina la actividad actual para que el usuario no pueda volver a ella con el botón "Atrás"
        }
    }

    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeObservers(){

    }

    private fun exchangeCurrentFragment(newFragment: Fragment, newMenuOption:String){
        currentFragment = newFragment

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,currentFragment)
            .commit()

        currentMenuOption = newMenuOption

        if (currentMenuOption == Constants.MENU_PROFILE ||
            currentMenuOption == Constants.MENU_MAP) {
            binding.topAppBar.clTopBar.visibility = View.GONE
        } else {
            binding.topAppBar.clTopBar.visibility = View.VISIBLE
        }

        highlightCurrentActivity(
            currentMenuOption!!,
            binding.appBarMain.btnActividades,
            binding.appBarMain.btnPerfil,
            binding.appBarMain.btnMapa,
            binding.appBarMain.btnAnuncios,
            binding.appBarMain.tvActividades,
            binding.appBarMain.tvPerfil,
            binding.appBarMain.tvMapa,
            binding.appBarMain.tvAnuncios)
    }

    private fun initializeListeners(){
        val currentActivity = this::class.java.simpleName
        Log.d("currentActivity",currentActivity)

        binding.appBarMain.btnActividades.setOnClickListener {
            selectMenuOption(Constants.MENU_ACTIVITIES)
        }

        binding.appBarMain.btnMapa.setOnClickListener {
            selectMenuOption(Constants.MENU_MAP)
        }

        binding.appBarMain.btnAnuncios.setOnClickListener {
            selectMenuOption(Constants.MENU_ANNOUNCEMENTS)
        }

        binding.appBarMain.btnPerfil.setOnClickListener {
            selectMenuOption(Constants.MENU_PROFILE)
        }
    }

    private fun selectMenuOption(menuOption:String){
        if(menuOption == currentMenuOption){
            return
        }

        when(menuOption){
            Constants.MENU_ACTIVITIES -> exchangeCurrentFragment(ActivitiesFragment(),Constants.MENU_ACTIVITIES)
            Constants.MENU_MAP -> exchangeCurrentFragment(MapFragment(),Constants.MENU_MAP)
            Constants.MENU_ANNOUNCEMENTS -> exchangeCurrentFragment(AnnouncementsFragment(),Constants.MENU_ANNOUNCEMENTS)
            Constants.MENU_PROFILE -> exchangeCurrentFragment(ProfileFragment(),Constants.MENU_PROFILE)
        }
    }

    private fun highlightCurrentActivity(
        currentMenuOption: String,
        btnActividades: ImageButton,
        btnPerfil: ImageButton,
        btnMapa: ImageButton,
        btnAnuncios: ImageButton,
        tvActividades: TextView,
        tvPerfil: TextView,
        tvMapa: TextView,
        tvAnuncios: TextView
    ) {
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        val slideOut = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        // Restablecer todos los botones a su estado original
        resetButtonState(btnActividades, tvActividades, R.drawable.ic_actividades, slideOut)
        resetButtonState(btnPerfil, tvPerfil, R.drawable.ic_perfil, slideOut)
        resetButtonState(btnMapa, tvMapa, R.drawable.ic_mapa, slideOut)
        resetButtonState(btnAnuncios, tvAnuncios, R.drawable.ic_anuncios, slideOut)

        // Aplicar animaciones y cambiar el color según el botón seleccionado
        when (currentMenuOption) {
            Constants.MENU_ACTIVITIES -> {
                selectButtonState(btnActividades, tvActividades, R.drawable.ic_actividades_selected, slideIn)
                binding.topAppBar.tvTopBar.text = getString(R.string.Actividades)
            }
            Constants.MENU_PROFILE -> selectButtonState(btnPerfil, tvPerfil, R.drawable.ic_perfil_selected, slideIn)
            Constants.MENU_MAP -> selectButtonState(btnMapa, tvMapa, R.drawable.ic_mapa_selected, slideIn)
            Constants.MENU_ANNOUNCEMENTS -> {
                selectButtonState(btnAnuncios, tvAnuncios, R.drawable.ic_anuncios_selected, slideIn)
                binding.topAppBar.tvTopBar.text = getString(R.string.Anuncios)
            }
        }
    }

    // Función para resetear el estado de un botón
    private fun resetButtonState(button: ImageButton, textView: TextView, defaultIcon: Int, slideOut: Animation) {
        button.setImageResource(defaultIcon)
        button.background = null
        textView.setTextColor(Color.GRAY)
        button.startAnimation(slideOut)
    }

    // Función para seleccionar un botón y aplicar el estado seleccionado
    private fun selectButtonState(button: ImageButton, textView: TextView, selectedIcon: Int, slideIn: Animation) {
        button.setImageResource(selectedIcon)
        button.background = getDrawable(R.drawable.bg_highlight_black)
        textView.setTextColor(Color.BLACK)
        button.startAnimation(slideIn)
    }

}