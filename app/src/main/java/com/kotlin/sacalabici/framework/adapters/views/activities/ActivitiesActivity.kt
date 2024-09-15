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
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class ActivitiesActivity: BaseActivity() {
    private lateinit var binding: AcivityActivitiesBinding
    // In your launch activity's onCreate()
//    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        initializeBinding()
        setupNavbar()


//        sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
//        val isUserRegistered = getSharedPreferences("user_prefs", MODE_PRIVATE).getBoolean("is_registered", false)
//
//        if (!isUserRegistered) {
            startActivity(Intent(this, SessionActivity::class.java))
            finish() // Optional: Finish the launch activity to prevent going back to it
//        }
    }

    private fun initializeBinding(){
        binding = AcivityActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}