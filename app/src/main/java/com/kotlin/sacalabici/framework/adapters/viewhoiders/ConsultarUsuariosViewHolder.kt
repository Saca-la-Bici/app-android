package com.kotlin.sacalabici.framework.adapters.viewhoiders

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.ItemUserBinding

class ConsultarUsuariosViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ConsultarUsuariosBase) {
        binding.RVuserName.text = item.usuario.username
    }
}