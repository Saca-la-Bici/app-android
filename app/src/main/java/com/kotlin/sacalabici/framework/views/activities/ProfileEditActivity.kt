package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
        setupGenderDropdown()
        setupBloodDropdown()

    }

    private fun initializeBinding(){
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupGenderDropdown() {
        val genderDropdownConfig = findViewById<AutoCompleteTextView>(R.id.genderDropDown)
        val genders = resources.getStringArray(R.array.genders)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, genders)
        genderDropdownConfig.setAdapter(arrayAdapter)

        val defaultValue = "Masculino"
        genderDropdownConfig.setText(defaultValue, false)

        val index = arrayAdapter.getPosition(defaultValue)
        if (index >= 0) {
            genderDropdownConfig.setSelection(index)
        }
    }

    private fun setupBloodDropdown() {
        val bloodDropdownConfig = findViewById<AutoCompleteTextView>(R.id.bloodDropDown)
        val bloodTypes = resources.getStringArray(R.array.bloodTypes)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, bloodTypes)
        bloodDropdownConfig.setAdapter(arrayAdapter)

        val defaultValue = "O+"
        bloodDropdownConfig.setText(defaultValue, false)

//        val index = arrayAdapter.getPosition(defaultValue)
//        if (index >= 0) {
////            bloodDropdownConfig.setSelection(index)
//        }
    }


}

