package com.kotlin.sacalabici.framework.viewholders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.ItemMedalBinding

class TotalMedalsViewHolder(
    private val binding: ItemMedalBinding,
) : RecyclerView.ViewHolder(binding.root) {

    // Método bind para vincular los datos del modelo con la UI
    fun bind(item: MedalBase) {
        binding.TVMedalName.text = item.name

        if (item.image.isNotEmpty()) {
            binding.IVMedal.visibility = View.VISIBLE
            getMedalImage(item.image, binding.IVMedal)
        } else {
            binding.IVMedal.visibility = View.GONE
        }
    }

    // Función para cargar la imagen de la medalla usando Glide
    private fun getMedalImage(url: String, imageView: ImageView) {
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
