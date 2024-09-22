package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository
import com.kotlin.sacalabici.databinding.FragmentRolStaffBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RolStaffFragment : Fragment() {

    private var _binding: FragmentRolStaffBinding? = null
    private val binding get() = _binding!!
    private val adapter: ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private lateinit var data: ArrayList<ConsultarUsuariosBase>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRolStaffBinding.inflate(inflater, container, false)
        getUsuarios()

        // Configurar botones
        binding.btnAdministradores.setOnClickListener {
            highlightCurrentFragment("Administradores")
        }
        binding.btnStaff.setOnClickListener {
            highlightCurrentFragment("Staff")
        }

        // Configurar el botón regreso
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun setUpRecyclerView(dataForList: ArrayList<ConsultarUsuariosBase>) {
        binding.RVViewUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.RVViewUsers.layoutManager = linearLayoutManager
        binding.RVViewUsers.adapter = adapter
    }

    private fun getUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val consultarUsuariosRepository = ConsultarUsuariosRepository()
            try {
                val usuarios = consultarUsuariosRepository.getUsuarios(limit = 0)
                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        data = ArrayList(usuarios)
                        mostrarUsuariosStaffYUsuarios(data)
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

    private fun mostrarUsuariosStaffYUsuarios(data: ArrayList<ConsultarUsuariosBase>) {
        val usuariosFiltrados = data.filter {
            it.rol.nombreRol == "Staff" || it.rol.nombreRol == "Usuario"
        }
        adapter.updateData(ArrayList(usuariosFiltrados))
        setUpRecyclerView(ArrayList(usuariosFiltrados))
    }

    private fun highlightCurrentFragment(currentFragment: String) {
        resetButtonStyles()

        when (currentFragment) {
            "Administradores" -> {
                binding.btnAdministradores.setTextColor(Color.YELLOW)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, RolAdministradorFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "Staff" -> {
                binding.btnStaff.setTextColor(Color.YELLOW)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, RolStaffFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun resetButtonStyles() {
        binding.btnAdministradores.setTextColor(Color.DKGRAY) // Define el color predeterminado
        binding.btnStaff.setTextColor(Color.DKGRAY) // Define el color predeterminado
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
