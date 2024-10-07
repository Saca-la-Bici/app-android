package com.kotlin.sacalabici.framework.views.activities.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import com.kotlin.sacalabici.framework.viewholders.DetailsViewHolder
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class DetailsActivity : AppCompatActivity() {
    private val activitiesViewModel: ActivitiesViewModel by viewModels()
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var detailsViewHolder: DetailsViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pasar el viewModel al crear DetailsViewHolder
        detailsViewHolder = DetailsViewHolder(binding, activitiesViewModel)

        val activityId = intent.getStringExtra("ACTIVITY_ID")
        activityId?.let {
            activitiesViewModel.getActivityById(it)
        }

        activitiesViewModel.selectedActivityLiveData.observe(this) { activity ->
            activity?.let { detailsViewHolder.bind(it) }
        }

        binding.btnBack.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }
}
