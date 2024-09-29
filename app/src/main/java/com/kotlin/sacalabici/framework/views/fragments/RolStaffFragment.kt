package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.FragmentRolStaffBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosViewModel
import com.kotlin.sacalabici.framework.viewmodel.session.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class RolStaffFragment : Fragment() {

    private var _binding: FragmentRolStaffBinding? = null
    private val binding get() = _binding!!
    private val adapter: ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private val viewModel: ConsultarUsuariosViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRolStaffBinding.inflate(inflater, container, false)

        // Obtener el firebaseUID
        val firebaseUID = authViewModel.getCurrentUserId()
        Log.d("UID", "Firebase UID: $firebaseUID")

        // Observamos los cambios en el ViewModel
        viewModel.usuarios.observe(viewLifecycleOwner, Observer { usuarios ->
            if (!usuarios.isNullOrEmpty()) {
                setUpRecyclerView(ArrayList(usuarios))
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Log.d("Error", message)
        })

        // Cargar usuarios
        viewModel.getUsuarios(roles = "Staff,Usuario", reset = true, firebaseUID = firebaseUID!!)

        // Configurar botones
        binding.btnAdministradores.setOnClickListener {
            highlightCurrentFragment("Administradores")
        }
        binding.btnStaff.setOnClickListener {
            highlightCurrentFragment("Staff")
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            private var searchJob: Job? = null

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob?.cancel()
                if (query != null) {
                    viewModel.searchUser(query, firebaseUID!!)
                } // Or appropriate ViewModel function
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(500) // Delay of 500 milliseconds (adjust as needed)
                    if (newText != null) {
                        viewModel.searchUser(newText, firebaseUID!!)
                    } // Or appropriate ViewModel function
                }
                return true
            }
        })

        // Agregar scroll listener
        binding.RVViewUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount <= (lastVisibleItem + 1)) {
                    // Si se llega al final, cargar más usuarios
                    viewModel.getUsuarios(firebaseUID = firebaseUID!!)
                }
            }
        })

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
        adapter.updateData(dataForList)
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
