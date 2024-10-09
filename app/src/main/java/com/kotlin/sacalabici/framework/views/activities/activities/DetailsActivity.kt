package com.kotlin.sacalabici.framework.views.activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import com.kotlin.sacalabici.framework.views.activities.StartRouteActivity
import com.kotlin.sacalabici.framework.viewholders.DetailsViewHolder
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class DetailsActivity: AppCompatActivity() {
    private val activitiesViewModel: ActivitiesViewModel by viewModels()
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var detailsViewHolder: DetailsViewHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailsViewHolder = DetailsViewHolder(binding)

        val activityId = intent.getStringExtra("ACTIVITY_ID")
        activityId?.let {
            activitiesViewModel.getActivityById(it)
        }

        activitiesViewModel.selectedActivityLiveData.observe(this) { activity ->
            activity?.let { detailsViewHolder.bind(it) }
        }

        binding.btnBack.setOnClickListener {
            Log.d("DetailsActivity", "Botón Back presionado")
            finish() // Regresar a la actividad anterior
        }

    }
}