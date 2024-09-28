package com.kotlin.sacalabici.framework.adapters.viewhoiders

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.ItemUserBinding

class ConsultarUsuariosViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ConsultarUsuariosBase) {
        binding.RVuserName.text = item.usuario.username

        // Cambia el texto y el color del botón según el rol
        if (item.rol.nombreRol == "Administrador") {
            binding.btnChangerol.text = "Eliminar" //Cambiar el texto del botón
            binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.lightGray))  // Cambia el color a gris
        } else if (item.rol.nombreRol == "Staff") {
            binding.btnChangerol.text = "Eliminar"
            binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.lightGray))
        }else if (item.rol.nombreRol == "Usuario") {
            binding.btnChangerol.text = "Agregar"
            binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.yellow))
        }
    }
}