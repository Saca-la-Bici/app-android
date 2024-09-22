package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityProfileEditBinding

class ProfileEditActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()

        setupGenderButton()
    }

    private fun initializeBinding(){
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun setupGenderButton() {
        val btnConfiguration = findViewById<ImageButton>(R.id.btn_gender)
        btnConfiguration.setOnClickListener {
            val singleItems = arrayOf("Mujer", "Hombre", "Otro", "Prefiero no decirlo")
            val checkedItem = 1

            MaterialAlertDialogBuilder(this) // 'this' refers to the context of the activity
                .setTitle(resources.getString(R.string.genderPE))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                    // Respond to positive button press
                }
                .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                    // Respond to item chosen
                }
                .show()
            }
        }
    }
