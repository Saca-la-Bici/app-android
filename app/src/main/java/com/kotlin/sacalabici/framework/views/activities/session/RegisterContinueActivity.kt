package com.kotlin.sacalabici.framework.views.activities.session
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivityRegisterUserContinueBinding
import com.kotlin.sacalabici.framework.viewmodel.session.RegisterContinueViewModel
class RegisterContinueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserContinueBinding
    private val registerContinueViewModel: RegisterContinueViewModel by viewModels()
    private var email: String? = null
    private var username: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeExtras()
        setupButtonListeners()
    }

    private fun navigateTo(activity: Class<*>) {
        val intent = Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun setupButtonListeners() {
        binding.BBack.setOnClickListener { navigateTo(SessionActivity::class.java) }
        binding.BContinue.setOnClickListener { setupContinueButton() }
    }

    private fun setupContinueButton() {
        val password = binding.TILPassword.editText?.text.toString()
        val confirmPassword = binding.TILVerifyPassword.editText?.text.toString()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.BContinue.isEnabled = true
        }, 5000)
        val errorMessage = registerContinueViewModel.arePasswordsValid(password, confirmPassword)
        if (errorMessage != null) {
            binding.BContinue.isEnabled = false
            Toast.makeText(this@RegisterContinueActivity, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this@RegisterContinueActivity, RegisterFinishActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("username", username)
            intent.putExtra("name", name)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }

    private fun initializeExtras() {
        email = intent.getStringExtra("email")
        username = intent.getStringExtra("username")
        name = intent.getStringExtra("name")
    }

    private fun initializeBinding() {
        binding = ActivityRegisterUserContinueBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}