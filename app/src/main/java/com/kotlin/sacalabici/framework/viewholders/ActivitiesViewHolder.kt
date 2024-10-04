package com.kotlin.sacalabici.framework.viewholders

import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import android.view.View
import android.widget.ImageView
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
    private val longClickListener: (Activity) -> Boolean,
    private val viewModel: ActivitiesViewModel
) : RecyclerView.ViewHolder(binding.root) {

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

        if (item.type == "Rodada") {
            binding.tvActivityLevel.visibility = View.VISIBLE
            binding.tvActivityLevel.text = item.nivel
        } else {
            binding.tvActivityLevel.visibility = View.GONE
        }

        // Verificar si el usuario está inscrito en la actividad
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseUID = firebaseUser?.uid

        if (firebaseUID != null) {
            // Verificar si el usuario está inscrito en la actividad
            val usuarioInscrito = item.usuariosInscritos.contains(firebaseUID)

            if (usuarioInscrito) {
                binding.btnJoin.text = "Cancelar inscripción"
                binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.gray))
                binding.btnJoin.setOnClickListener {
                    Log.d("ActivitiesViewHolder", "Cancelar inscripción. Activity ID: ${item.id}, Type: ${item.type}")
                    viewModel.postCancelarInscripcion(item.id, item.type) // Nueva función para cancelar inscripción
                }
            } else {
                binding.btnJoin.text = "Inscribirse"
                binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.yellow))
                binding.btnJoin.setOnClickListener {
                    Log.d("ActivitiesViewHolder", "Inscribirse. Activity ID: ${item.id}, Type: ${item.type}")
                    viewModel.postInscribirActividad(item.id, item.type)
                }
            }
        } else {
            Log.e("ActivitiesViewHolder", "No Firebase user authenticated.")
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

