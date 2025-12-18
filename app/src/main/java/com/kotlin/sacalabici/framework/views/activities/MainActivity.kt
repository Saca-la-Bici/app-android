package com.kotlin.sacalabici.framework.views.activities
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.session.AuthState
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.databinding.ActivityMainBinding
import com.kotlin.sacalabici.databinding.ActivityRegisterUserFinishBinding
import com.kotlin.sacalabici.framework.viewmodel.session.AuthViewModel
import com.kotlin.sacalabici.framework.views.activities.session.LoginFinishActivity
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import com.kotlin.sacalabici.framework.views.fragments.ActivitiesFragment
import com.kotlin.sacalabici.framework.views.fragments.AnnouncementsFragment
import com.kotlin.sacalabici.framework.views.fragments.MapFragment
import com.kotlin.sacalabici.framework.views.fragments.ProfileFragment
import com.kotlin.sacalabici.utils.Constants
class MainActivity: AppCompatActivity() {
    private lateinit var currentFragment: Fragment
    private var currentMenuOption:String?= null
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var tokenManager: FirebaseTokenManager
    private val authViewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            savePermissionDeniedState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeListeners()
        initializeViewModel()
        checkUserSession()
        observeAuthState()
        exchangeCurrentFragment(ActivitiesFragment(), Constants.MENU_ACTIVITIES)
        moveHighlightToButton(binding.appBarMain.btnActividades)
        firebaseAuth = FirebaseAuth.getInstance()
        tokenManager = FirebaseTokenManager(firebaseAuth)
        tokenManager.getIdToken()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(applicationContext as Application)
    }
    private fun observeAuthState() {
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthState.Success -> {
                }
                is AuthState.Error -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.IncompleteProfile -> {
                    navigateTo(SessionActivity::class.java)
                }
                is AuthState.CompleteProfile -> {
                }
                is AuthState.VerificationSent -> {
                    Toast.makeText(this, authState.message, Toast.LENGTH_SHORT).show()
                }
                AuthState.Cancel -> TODO()
                AuthState.SignedOut -> TODO()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled() && !isPermissionDenied()) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun navigateTo(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }
    private fun initializeViewModel() {
        authViewModel.initialize(
            FirebaseAuth.getInstance(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.REQUEST_ID_TOKEN)
                .requestEmail()
                .build(),
            this
        )
    }
    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun checkUserSession(){
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && !user.isEmailVerified) {
            navigateTo(SessionActivity::class.java)
            return
        }

        if (user == null) {
            navigateTo(SessionActivity::class.java)
        }
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
    private fun savePermissionDeniedState() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("notification_permission_denied", true)
            apply()
        }
    }
    private fun isPermissionDenied(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notification_permission_denied", false)
    }
    override fun onStart() {
        super.onStart()
        authViewModel.startAuthStateListener()
    }
}