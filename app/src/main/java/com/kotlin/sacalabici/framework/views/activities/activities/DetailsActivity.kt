package com.kotlin.sacalabici.framework.views.activities.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import com.kotlin.sacalabici.framework.viewholders.DetailsViewHolder
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class DetailsActivity : AppCompatActivity() {
    private val activitiesViewModel: ActivitiesViewModel by viewModels()
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var detailsViewHolder: DetailsViewHolder

    private val sharedPreferences by lazy {
        this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList() ?: emptyList()
        val activityId = intent.getStringExtra("ACTIVITY_ID")

        initializeViewHolder(activityId!!, storedPermissions)
        observeLiveData()
        setupClickListeners()

        // Obtener actividad por ID
        activitiesViewModel.getActivityById(activityId)
    }

    private fun initializeViewHolder(activityId: String, storedPermissions: List<String>) {
        detailsViewHolder = DetailsViewHolder(binding, activitiesViewModel, activityId, storedPermissions)
    }

    private fun observeLiveData() {
        activitiesViewModel.selectedActivityLiveData.observe(this) { activity ->
            if (activity != null) {
                detailsViewHolder.bind(activity)
                binding.clDetailActivity.visibility = View.VISIBLE // Mostrar vista después del binding
            } else {
                Toast.makeText(this, getString(R.string.noActivity), Toast.LENGTH_SHORT).show()
                finish() // Regresar a la actividad anterior si no hay actividad
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }
}
