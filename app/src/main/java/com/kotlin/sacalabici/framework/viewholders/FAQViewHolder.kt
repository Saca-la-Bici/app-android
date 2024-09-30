package com.kotlin.sacalabici.framework.viewholders

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.databinding.ItemFaqBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewHolder(private val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {

    // Asocia un objeto FAQ con una vista
    // Establece el texto de la pregunta en el TextView

    fun bind(item: com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase, context: Context) {
        binding.preguntafrecuentedisplay.text = item.Pregunta
    }

}