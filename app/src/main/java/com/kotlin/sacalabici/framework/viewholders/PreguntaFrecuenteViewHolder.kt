package com.kotlin.sacalabici.framework.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.databinding.ItemFaqBinding

class PreguntaFrecuenteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemFaqBinding.bind(view)

    fun render(preguntaFrecuenteModel: PreguntaFrecuente) {
        binding.preguntafrecuentedisplay.text = preguntaFrecuenteModel.Pregunta
    }
}
