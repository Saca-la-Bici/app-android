package com.kotlin.sacalabici.framework.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.AcivityActivitiesBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class ActivitiesActivity: BaseActivity() {
    private lateinit var binding: AcivityActivitiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setupNavbar()
    }

    private fun initializeBinding(){
        binding = AcivityActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}