package com.kotlin.sacalabici.framework.viewholders

import com.bumptech.glide.Glide
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsViewHolder(private val binding: ActivityDetailsBinding) {

    fun bind(activity: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(activity.date)
        binding.tvActivityTitle.text = activity.title
        binding.tvActivityLevel.text = activity.nivel
        binding.tvPeopleCount.text = activity.peopleEnrolled.toString()

        if (activity.imageURL != null) {
            Glide.with(binding.root.context)
                .load(activity.imageURL)
                .into(binding.ivActivityImage)
        }

        binding.tvActivityDate.text = binding.root.context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = binding.root.context.getString(R.string.activity_time_list, activity.time)
        binding.tvActivityDuration.text = binding.root.context.getString(R.string.activity_duration_list, activity.duration)
        binding.tvActivityLocation.text = binding.root.context.getString(R.string.activity_location_list, activity.location)
        binding.tvActivityDistance.text = binding.root.context.getString(R.string.activity_distance, activity.distancia)
        binding.tvActivityDescription.text = activity.description

        binding.btnJoin.setOnClickListener {
            // Lógica para unirse a la actividad
        }

        binding.btnStart.setOnClickListener {
            // Lógica para iniciar la actividad
        }

        binding.btnRuta.setOnClickListener {
            // Lógica para ver la ruta de la actividad
        }
    }
}
