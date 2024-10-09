package com.kotlin.sacalabici.framework.viewholders

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.ActivityBase
import com.kotlin.sacalabici.databinding.ItemEventBinding
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileViewHolder (
    private val binding: ItemEventBinding,
    private val clickListener: (ActivityBase) -> Unit,
    private val viewModel: ProfileViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ActivityBase) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(item.date)

        val context = binding.root.context

        binding.tvEventTitle.text = item.title
        binding.tvEventDate.text = context.getString(R.string.activity_date_list, formattedDate)
        binding.tvEventTime.text = context.getString(R.string.activity_time_list, item.time)
        binding.tvEventDuration.text = context.getString(R.string.activity_duration_list, item.duration)
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()
        binding.tvEventLocation.text = context.getString(R.string.activity_location_list, item.location)

        if (item.type == "Rodada") {
            binding.tvEventLevel.visibility = View.VISIBLE
            binding.tvEventLevel.text = item.nivel

            val levelColor = when (item.nivel) {
                "Nivel 1" -> ContextCompat.getColor(context, R.color.level1)
                "Nivel 2" -> ContextCompat.getColor(context, R.color.level2)
                "Nivel 3" -> ContextCompat.getColor(context, R.color.level3)
                "Nivel 4" -> ContextCompat.getColor(context, R.color.level4)
                "Nivel 5" -> ContextCompat.getColor(context, R.color.level5)
                else -> ContextCompat.getColor(context, R.color.gray)
            }
            val background = binding.tvEventLevel.background as GradientDrawable
            background.setColor(levelColor)
        } else {
            binding.tvEventLevel.visibility = View.GONE
        }

        // Configurar clic para desplegar detalles
        binding.tvEventDetails.setOnClickListener {
            clickListener(item)
        }

    }
}