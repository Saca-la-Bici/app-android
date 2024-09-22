package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository
import com.kotlin.sacalabici.databinding.FragmentModifyRolBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModifyRolesFragment : Fragment() {

    private var _binding: FragmentModifyRolBinding? = null
    private val binding get() = _binding!!
    private val adapter: ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private lateinit var data: ArrayList<ConsultarUsuariosBase>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModifyRolBinding.inflate(inflater, container, false)

        // Llamar a la función para obtener los usuarios
        getUsuarios()

        return binding.root
    }

    private fun getUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val consultarUsuariosRepository = ConsultarUsuariosRepository()
            try {
                val usuarios = consultarUsuariosRepository.getUsuarios(limit = 0)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        data = ArrayList(usuarios) // Guarda todos los usuarios
                        mostrarUsuariosAdministradoresYUsuarios(data) // Muestra por defecto
                    } else {
                        Log.d("Error", "La lista de usuarios está vacía o es nula.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "No se pudieron obtener los usuarios: ${e.message}")
            }
        }
    }

    private fun mostrarUsuariosAdministradoresYUsuarios(data: ArrayList<ConsultarUsuariosBase>) {
        val usuariosFiltrados = data.filter {
            it.rol.nombreRol == "Administrador" || it.rol.nombreRol == "Usuario"
        }
        setUpRecyclerView(ArrayList(usuariosFiltrados))
    }

    private fun mostrarUsuariosStaffYUsuarios(data: ArrayList<ConsultarUsuariosBase>) {
        val usuariosFiltrados = data.filter {
            it.rol.nombreRol == "Staff" || it.rol.nombreRol == "Usuario"
        }
        setUpRecyclerView(ArrayList(usuariosFiltrados))
    }

    private fun setUpRecyclerView(dataForList: ArrayList<ConsultarUsuariosBase>) {
        binding.RVViewUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RVViewUsers.layoutManager = linearLayoutManager
        adapter.ConsultarUsuariosAdapter(dataForList)
        binding.RVViewUsers.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Liberar el binding
    }
}
