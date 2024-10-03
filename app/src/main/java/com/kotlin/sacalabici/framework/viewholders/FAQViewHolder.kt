package com.kotlin.sacalabici.framework.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.ItemFaqBinding

class FAQViewHolder(
    private val binding: ItemFaqBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FAQBase) {
        binding.preguntafrecuentedisplay.text = item.Pregunta
    }
}
