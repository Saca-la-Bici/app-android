package com.kotlin.sacalabici.framework.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.databinding.ActivityMainBinding
import com.kotlin.sacalabici.framework.adapters.views.activities.Session.SessionActivity
import com.kotlin.sacalabici.framework.adapters.views.fragments.ActivitiesFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.AnnouncementsFragment
import com.kotlin.sacalabici.framework.views.fragments.MapFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.ProfileFragment
import com.kotlin.sacalabici.utils.Constants


class MainActivity: AppCompatActivity() {
    private lateinit var currentFragment: Fragment

    private var currentMenuOption:String?= null

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var tokenManager: FirebaseTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        initializeObservers()
        initializeListeners()
        exchangeCurrentFragment(ActivitiesFragment(), Constants.MENU_ACTIVITIES)
        moveHighlightToButton(binding.appBarMain.btnActividades)

        firebaseAuth = FirebaseAuth.getInstance()
        tokenManager = FirebaseTokenManager(firebaseAuth)
        tokenManager.getIdToken()

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
    }

    private fun initializeListeners(){

        binding.appBarMain.btnActividades.setOnClickListener {
            selectMenuOption(Constants.MENU_ACTIVITIES)
            moveHighlightToButton(binding.appBarMain.btnActividades)
        }

        binding.appBarMain.btnMapa.setOnClickListener {
            selectMenuOption(Constants.MENU_MAP)
            moveHighlightToButton(binding.appBarMain.btnMapa)
        }

        binding.appBarMain.btnAnuncios.setOnClickListener {
            selectMenuOption(Constants.MENU_ANNOUNCEMENTS)
            moveHighlightToButton(binding.appBarMain.btnAnuncios)
        }

        binding.appBarMain.btnPerfil.setOnClickListener {
            selectMenuOption(Constants.MENU_PROFILE)
            moveHighlightToButton(binding.appBarMain.btnPerfil)
        }

    }

    private fun selectMenuOption(menuOption:String){
        if(menuOption == currentMenuOption){
            return
        }

        when(menuOption){
            Constants.MENU_ACTIVITIES -> {
                exchangeCurrentFragment(ActivitiesFragment(),Constants.MENU_ACTIVITIES)
                highlightCurrentActivity(menuOption,
                    binding.appBarMain.btnActividades,
                    binding.appBarMain.tvActividades)
            }
            Constants.MENU_MAP -> {
                exchangeCurrentFragment(MapFragment(),Constants.MENU_MAP)
                highlightCurrentActivity(menuOption,
                    binding.appBarMain.btnMapa,
                    binding.appBarMain.tvMapa)
            }
            Constants.MENU_ANNOUNCEMENTS -> {
                exchangeCurrentFragment(AnnouncementsFragment(),Constants.MENU_ANNOUNCEMENTS)
                highlightCurrentActivity(menuOption,
                    binding.appBarMain.btnAnuncios,
                    binding.appBarMain.tvAnuncios)
            }
            Constants.MENU_PROFILE -> {
                exchangeCurrentFragment(ProfileFragment(),Constants.MENU_PROFILE)
                highlightCurrentActivity(menuOption,
                    binding.appBarMain.btnPerfil,
                    binding.appBarMain.tvPerfil)
            }
        }
    }

    private fun highlightCurrentActivity(
        currentMenuOption: String,
        buttonClicked: ImageButton,
        textClicked: TextView,
    ) {

        // Restablecer todos los botones a su estado original
        resetButtonState(
            binding.appBarMain.btnActividades,
            binding.appBarMain.tvActividades,
            R.drawable.ic_actividades)
        resetButtonState(
            binding.appBarMain.btnPerfil,
            binding.appBarMain.tvPerfil,
            R.drawable.ic_profile)
        resetButtonState(
            binding.appBarMain.btnMapa,
            binding.appBarMain.tvMapa,
            R.drawable.ic_mapa)
        resetButtonState(
            binding.appBarMain.btnAnuncios,
            binding.appBarMain.tvAnuncios,
            R.drawable.ic_anuncios)

        // Aplicar animaciones y cambiar el color según el botón seleccionado
        when (currentMenuOption) {
            Constants.MENU_ACTIVITIES -> {
                selectButtonState(buttonClicked, textClicked, R.drawable.ic_actividades_selected)
                binding.topAppBar.tvTopBar.text = getString(R.string.Actividades)
            }
            Constants.MENU_PROFILE -> selectButtonState(buttonClicked, textClicked, R.drawable.ic_profile_selected)
            Constants.MENU_MAP -> selectButtonState(buttonClicked, textClicked, R.drawable.ic_mapa_selected)
            Constants.MENU_ANNOUNCEMENTS -> {
                selectButtonState(buttonClicked, textClicked, R.drawable.ic_anuncios_selected)
                binding.topAppBar.tvTopBar.text = getString(R.string.Anuncios)
            }
        }
    }

    // Función para resetear el estado de un botón
    private fun resetButtonState(button: ImageButton, textView: TextView, defaultIcon: Int) {
        button.setImageResource(defaultIcon)
        textView.setTextColor(Color.GRAY)
    }

    // Función para seleccionar un botón y aplicar el estado seleccionado
    private fun selectButtonState(button: ImageButton, textView: TextView, selectedIcon: Int) {
        button.setImageResource(selectedIcon)
        textView.setTextColor(Color.BLACK)
    }

    private fun moveHighlightToButton(targetButton: ImageButton) {
        val constraintLayout = binding.appBarMain.clAppBar
        val constraintSet = ConstraintSet()

        constraintSet.clone(constraintLayout)

        // Conectar el highlightView al botón seleccionado
        constraintSet.connect(R.id.vHighlightView, ConstraintSet.START, targetButton.id, ConstraintSet.START)
        constraintSet.connect(R.id.vHighlightView, ConstraintSet.END, targetButton.id, ConstraintSet.END)

        // Aplicar la transición
        val transition = ChangeBounds()
        transition.duration = 300 // Cambiar duración si es necesario para debugging
        TransitionManager.beginDelayedTransition(constraintLayout, transition)

        // Aplicar el ConstraintSet
        constraintSet.applyTo(constraintLayout)
    }
}