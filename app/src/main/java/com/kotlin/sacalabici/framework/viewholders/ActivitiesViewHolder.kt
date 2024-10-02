package com.kotlin.sacalabici.framework.viewholders

import android.util.Log
import android.view.View
import android.widget.ImageView
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
    private val longClickListener: (Activity) -> Boolean,
    private val viewModel: ActivitiesViewModel
): RecyclerView.ViewHolder(binding.root){



    fun bind(item: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(item.date)

        binding.tvActivityTitle.text = item.title
        binding.tvActivityDate.text = binding.root.context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = binding.root.context.getString(R.string.activity_time_list, item.time)
        binding.tvActivityDuration.text = binding.root.context.getString(R.string.activity_duration_list, item.duration)
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()
        binding.tvActivityLocation.text = binding.root.context.getString(R.string.activity_location_list, item.location)

        binding.root.setOnLongClickListener {
            longClickListener(item)
        }

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

        binding.btnJoin.setOnClickListener(){
            Log.d("ITEM", "$item")
            //Falta el id del usuario
            val actividadId = item.id // Este 'item' es tu objeto 'Activity'
            val tipo = item.type // Este 'type' ya está en tu clase 'Activity'
            // Llamar al ViewModel para inscribir la actividad
            Log.d("ActivitiesViewHolder", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
            viewModel.postInscribirActividad(actividadId, tipo)
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
