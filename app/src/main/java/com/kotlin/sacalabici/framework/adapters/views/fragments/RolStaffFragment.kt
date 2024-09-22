package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.FragmentRolStaffBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosViewModel


class RolStaffFragment : Fragment() {

    private var _binding: FragmentRolStaffBinding? = null
    private val binding get() = _binding!!
    private val adapter: ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private val viewModel: ConsultarUsuariosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRolStaffBinding.inflate(inflater, container, false)

        // Observamos los cambios en el ViewModel
        viewModel.usuarios.observe(viewLifecycleOwner, Observer { usuarios ->
            if (!usuarios.isNullOrEmpty()) {
                mostrarUsuariosStaffYUsuarios(ArrayList(usuarios))
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Log.d("Error", message)
        })

        // Cargar usuarios
        viewModel.getUsuarios()

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
        binding.btnAdministradores.setTextColor(Color.DKGRAY)
        binding.btnStaff.setTextColor(Color.DKGRAY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
