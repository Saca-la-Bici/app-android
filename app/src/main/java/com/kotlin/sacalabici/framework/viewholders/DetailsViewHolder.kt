package com.kotlin.sacalabici.framework.viewholders

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.set
import com.bumptech.glide.Glide
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import com.kotlin.sacalabici.framework.views.activities.StartRouteActivity
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsViewHolder(private val binding: ActivityDetailsBinding) {

    fun bind(activity: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(activity.date)
        binding.tvActivityTitle.text = activity.title

        if (activity.nivel != null) {
            binding.tvActivityLevel.visibility = View.VISIBLE
            binding.tvActivityLevel.text = activity.nivel
        } else {
            binding.tvActivityLevel.visibility = View.GONE
        }

        binding.tvActivityLevel.text = activity.nivel
        binding.tvPeopleCount.text = activity.peopleEnrolled.toString()

        if (activity.imageURL != null) {
            binding.ivActivityImage.visibility = View.VISIBLE
            Glide.with(binding.root.context)
                .load(activity.imageURL)
                .into(binding.ivActivityImage)
        } else {
            binding.ivActivityImage.visibility = View.GONE
        }

        binding.tvActivityDate.text = binding.root.context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = binding.root.context.getString(R.string.activity_time_list, activity.time)
        binding.tvActivityDuration.text = binding.root.context.getString(R.string.activity_duration_list, activity.duration)
        binding.tvActivityLocation.text = binding.root.context.getString(R.string.activity_location_list, activity.location)
        binding.tvActivityDescription.text = activity.description

        binding.btnJoin.setOnClickListener {
            // Lógica para unirse a la actividad
        }

        if (activity.type == "Rodada") {
            binding.tvActivityDistance.visibility  = View.VISIBLE
            binding.tvActivityDistance.text = binding.root.context.getString(R.string.activity_distance, activity.distancia)
            binding.tvActivityRenta.visibility = View.VISIBLE

            // Enlace después del texto "Renta de Bicicletas: "
            val text = binding.root.context.getString(R.string.activity_rent) + " "
            val linkText = "Te llevará a un Google Forms"
            val spannableString = SpannableString(text + linkText)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1X0EGsAQzYx5u6j8o5HptuchHgPB1_uflR5H4MFhqq5U/viewform?edit_requested=true"))
                    widget.context.startActivity(intent)
                }
            }

            spannableString.setSpan(clickableSpan, text.length, text.length + linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val colorSpan = ForegroundColorSpan(Color.parseColor("#7DA68D"))
            spannableString.setSpan(colorSpan, text.length, text.length + linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.tvActivityRenta.text = spannableString
            binding.tvActivityRenta.movementMethod = LinkMovementMethod.getInstance()

        } else {
            binding.tvActivityDistance.visibility = View.GONE
            binding.tvActivityRenta.visibility = View.GONE
        }

        if (activity.type == "Rodada"){
            binding.btnStart.visibility = View.VISIBLE
            binding.btnStart.setOnClickListener {
                val intent = Intent(binding.root.context, StartRouteActivity::class.java) // Cambiado aquí
                binding.root.context.startActivity(intent)
            }
        } else {
            binding.btnStart.visibility = View.GONE
        }

        if (activity.type == "Rodada") {
            binding.btnRuta.visibility = View.VISIBLE
            binding.btnRuta.setOnClickListener {
                // Lógica para ver la ruta de la actividad
            }
        } else {
            binding.btnRuta.visibility = View.GONE
        }
    }
}
