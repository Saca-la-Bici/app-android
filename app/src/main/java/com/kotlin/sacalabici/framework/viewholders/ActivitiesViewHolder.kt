package com.kotlin.sacalabici.framework.viewholders

import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ItemActivityBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ActivitiesViewHolder(
    private val binding: ItemActivityBinding,
    private val clickListener: (Activity) -> Unit,
    private val viewModel: ActivitiesViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(item.date)

        val context = binding.root.context

        binding.tvActivityTitle.text = item.title
        binding.tvActivityDate.text = context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = context.getString(R.string.activity_time_list, item.time)
        binding.tvActivityDuration.text = context.getString(R.string.activity_duration_list, item.duration)
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()
        binding.tvActivityLocation.text = context.getString(R.string.activity_location_list, item.location)

        if (item.imageURL != null) {
            binding.ivActivityImage.visibility = View.VISIBLE
            getActivityImage(item.imageURL, binding.ivActivityImage)
        } else {
            binding.ivActivityImage.visibility = View.GONE
        }

        if (item.type == "Rodada") {
            binding.tvActivityLevel.visibility = View.VISIBLE
            binding.tvActivityLevel.text = item.nivel

            val levelColor = when (item.nivel) {
                "Nivel 1" -> ContextCompat.getColor(context,R.color.level1)
                "Nivel 2" -> ContextCompat.getColor(context,R.color.level2)
                "Nivel 3" -> ContextCompat.getColor(context,R.color.level3)
                "Nivel 4" -> ContextCompat.getColor(context,R.color.level4)
                "Nivel 5" -> ContextCompat.getColor(context,R.color.level5)
                else -> ContextCompat.getColor(context, R.color.gray)
            }
            val background = binding.tvActivityLevel.background as GradientDrawable
            background.setColor(levelColor)
        } else {
            binding.tvActivityLevel.visibility = View.GONE
        }

        // Configurar clic para desplegar detalles
        binding.tvActivityDetails.setOnClickListener {
            clickListener(item)
        }

    }

    // Función para cargar la imagen
    private fun getActivityImage(url: String, imageView: ImageView) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .priority(Priority.HIGH)

        Glide.with(itemView.context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }
}

