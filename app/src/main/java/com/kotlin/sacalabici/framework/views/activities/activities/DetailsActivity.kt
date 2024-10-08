package com.kotlin.sacalabici.framework.views.activities.activities

import android.os.Bundle
import android.util.Log
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

        val activityId = intent.getStringExtra("ACTIVITY_ID")
        // Pasar el viewModel al crear DetailsViewHolder
        detailsViewHolder = DetailsViewHolder(binding, activitiesViewModel, activityId!!)

        Log.d("testing", "intent: ${activityId}")
        activityId?.let {
            activitiesViewModel.getActivityById(it)
        }

        activitiesViewModel.selectedActivityLiveData.observe(this) { activity ->
            activity?.let { detailsViewHolder.bind(it) }
            if (activity != null) {
                Log.d("testing", "viewmodel: ${activity.id}")
            }
        }

        binding.btnBack.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }
}
