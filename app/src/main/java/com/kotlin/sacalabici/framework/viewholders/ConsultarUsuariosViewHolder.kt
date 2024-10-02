package com.kotlin.sacalabici.framework.adapters.viewhoiders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        } else if (item.rol.nombreRol == "Usuario") {
            binding.btnChangerol.text = "Agregar"
            binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.yellow))
        }

        // Cargar imagen de perfil usando Glide
        val profileImageUrl = item.imagenPerfil

        // Si la URL de la imagen no es nula ni vacía, cargarla con Glide
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(profileImageUrl) // Cargar la imagen desde la URL
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cachear la imagen
                .placeholder(R.drawable.baseline_person_24) // Imagen por defecto mientras se carga
                .error(R.drawable.baseline_person_24) // Imagen por defecto en caso de error
                .into(binding.RVuserImage) // Colocar la imagen en el ImageView
        } else {
            // Si la URL es nula o vacía, usar la imagen por defecto
            binding.RVuserImage.setImageResource(R.drawable.baseline_person_24)
        }
    }
}
