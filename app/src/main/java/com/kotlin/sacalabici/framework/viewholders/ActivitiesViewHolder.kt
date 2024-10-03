package com.kotlin.sacalabici.framework.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ItemActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ActivitiesViewHolder(
    private val binding: ItemActivityBinding,
    private val clickListener: (Activity) -> Unit
): RecyclerView.ViewHolder(binding.root){

    fun bind(item: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(item.date)

        binding.tvActivityTitle.text = item.title
        binding.tvActivityDate.text = binding.root.context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = binding.root.context.getString(R.string.activity_time_list, item.time)
        binding.tvActivityDuration.text = binding.root.context.getString(R.string.activity_duration_list, item.duration)
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()
        binding.tvActivityLocation.text = binding.root.context.getString(R.string.activity_location_list, item.location)

        if (item.imageURL != null) {
            binding.ivActivityImage.visibility = View.VISIBLE
            getActivityImage(item.imageURL, binding.ivActivityImage)
        } else {
            binding.ivActivityImage.visibility = View.GONE
        }

        if (item.type == "Rodada"){
            binding.tvActivityLevel.visibility = View.VISIBLE
            binding.tvActivityLevel.text = item.nivel
        } else {
            binding.tvActivityLevel.visibility = View.GONE
        }

        // Configurar clic para desplegar detalles
        binding.tvActivityDetails.setOnClickListener {
            clickListener(item)
        }

    }

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
