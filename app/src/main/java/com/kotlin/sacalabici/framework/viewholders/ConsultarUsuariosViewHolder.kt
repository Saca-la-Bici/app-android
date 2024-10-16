// Consultar Usuario View Holder
package com.kotlin.sacalabici.framework.viewholders

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.ItemUserBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole.ModifyRoleViewModel
import com.kotlin.sacalabici.framework.viewmodel.profile.ConsultarUsuariosViewModel

class ConsultarUsuariosViewHolder(
    val binding: ItemUserBinding,
    private val modifyRoleViewModel: ModifyRoleViewModel, // ViewModel para cambiar el rol
    private val currentFragmentRole: String, // Rol del fragmento actual
    private val getUsuariosViewModel: ConsultarUsuariosViewModel,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ConsultarUsuariosBase) {
        binding.RVuserName.text = item.usuario.username

        // Cambiar el texto y el color del botón según el rol del usuario
        when (item.rol.nombreRol) {
            "Administrador", "Staff" -> {
                binding.btnChangerol.text = "Eliminar"
                binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.lightGray))
            }
            "Usuario" -> {
                binding.btnChangerol.text = "Agregar"
                binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.yellow))
            }
        }

        // Configurar el evento de clic en el botón para cambiar el rol
        binding.btnChangerol.setOnClickListener {
            val nuevoRol =
                if (item.rol.nombreRol == "Usuario") {
                    // Cambiar al rol del fragmento actual
                    binding.btnChangerol.text = "Eliminar"
                    binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.lightGray))
                    currentFragmentRole
                } else {
                    // Cambiar a "Usuario"
                    binding.btnChangerol.text = "Agregar"
                    binding.btnChangerol.setBackgroundColor(itemView.context.getColor(R.color.yellow))
                    "66e882740f14ea86304fa973"
                }

            // Llamar al ViewModel para cambiar el rol del usuario
            modifyRoleViewModel.patchRole(item.usuario, nuevoRol)

            Toast.makeText(itemView.context, "Rol cambiado exitosamente", Toast.LENGTH_SHORT).show()
            binding.btnChangerol.isEnabled = false
            getUsuariosViewModel.getUsuarios(reset = true)
        }

        // Cargar imagen de perfil usando Glide
        val profileImageUrl = item.usuario.imagenPerfil

        // Si la URL de la imagen no es nula ni vacía, cargarla con Glide
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide
                .with(itemView.context)
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
