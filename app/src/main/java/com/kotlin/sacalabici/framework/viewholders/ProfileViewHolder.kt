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
        // Define the date format used in the API response
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val formattedDate = try {
            val date = apiDateFormat.parse(item.date)
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            item.date  // Fallback to the original date string if parsing fails
        }

        val context = binding.root.context

        binding.tvEventTitle.text = item.title
        binding.tvEventDate.text = context.getString(R.string.activity_date_list, formattedDate)
        binding.tvEventTime.text = context.getString(R.string.activity_time_list, item.time)
        binding.tvEventDuration.text = context.getString(R.string.activity_duration_list, item.duration)
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()
        binding.tvEventLocation.text = context.getString(R.string.activity_location_list, item.location)

        // Configurar clic para desplegar detalles
        binding.tvEventDetails.setOnClickListener {
            clickListener(item)
        }
    }
}