package com.kotlin.sacalabici.framework.adapters.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.databinding.ItemRutaBinding

class RutasViewHolder(private val binding: ItemRutaBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RouteBase, context: Context) {
        binding.TVTitulo.text = item.titulo
        binding.TVDistancia.text = item.distancia
        binding.TVTiempo.text = item.tiempo
        binding.TVNivel.text = item.nivel
    }
}