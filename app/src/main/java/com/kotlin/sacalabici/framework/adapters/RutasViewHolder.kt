package com.kotlin.sacalabici.framework.adapters

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.ItemRutaBinding

class RutasViewHolder(private val binding: ItemRutaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RutasBase) {
            binding.TVTitulo.text = item.titulo
            binding.TVDistancia.text = item.distancia
            binding.TVTiempo.text = item.tiempo
            binding.TVNivel.text = item.nivel
        }
    }
}