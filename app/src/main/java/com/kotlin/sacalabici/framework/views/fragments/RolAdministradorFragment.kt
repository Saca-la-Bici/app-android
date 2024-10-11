package com.kotlin.sacalabici.framework.adapters.views.fragments

import ConsultarUsuariosAdapter
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
import com.kotlin.sacalabici.databinding.FragmentRolAdministradorBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole.ModifyRoleViewModel
import com.kotlin.sacalabici.framework.viewmodel.profile.ConsultarUsuariosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RolAdministradorFragment : Fragment() {
    private var _binding: FragmentRolAdministradorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConsultarUsuariosViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: ConsultarUsuariosAdapter
    private var currentFragmentRole: String = "66e882600f14ea86304fa971"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRolAdministradorBinding.inflate(inflater, container, false)

        adapter =
            ConsultarUsuariosAdapter(
                modifyRoleViewModel = ModifyRoleViewModel(),
                consultarUsuariosViewModel = viewModel, // Pasar el ViewModel aquí
                currentFragmentRole = currentFragmentRole,
            )

        setUpRecyclerView(ArrayList())

        // Observamos los cambios en el ViewModel
        viewModel.usuarios.observe(
            viewLifecycleOwner,
            Observer { usuarios ->
                if (!usuarios.isNullOrEmpty()) {
                    setUpRecyclerView(ArrayList(usuarios))
                    binding.RVViewUsers.scrollToPosition(viewModel.scrollPosition) // Restaurar posición aquí
                }
            },
        )

        viewModel.errorMessage.observe(
            viewLifecycleOwner,
            Observer { message ->
                Log.d("Error", message)
            },
        )

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Cargar usuarios
        viewModel.getUsuarios(roles = "Administrador,Usuario", reset = true)

        // Configurar botones
        binding.btnAdministradores.setOnClickListener {
            viewModel.scrollPosition = 0 // Reiniciar posición
            highlightCurrentFragment("Administradores")
        }
        binding.btnStaff.setOnClickListener {
            viewModel.scrollPosition = 0 // Reiniciar posición
            highlightCurrentFragment("Staff")
        }

        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                private var searchJob: Job? = null

                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchJob?.cancel()
                    if (query != null) {
                        viewModel.isSearching = true // Entrar en modo de búsqueda
                        viewModel.searchUser(query, roles = "Administrador,Usuario") // Limitar la búsqueda solo a Staff y Usuario
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    searchJob =
                        coroutineScope.launch {
                            delay(500)
                            if (!newText.isNullOrEmpty()) {
                                // Si hay texto en el buscador, ejecuta la búsqueda
                                viewModel.searchUser(newText, roles = "Administrador,Usuario") // Limitar la búsqueda solo a Staff y Usuario
                            } else { // Si el texto está vacío, vuelve al estado paginado
                                viewModel.isSearching = false // Salir del modo de búsqueda
                                viewModel.getUsuarios(
                                    roles = "Administrador,Usuario",
                                    reset = true,
                                ) // Recargar la lista original
                            }
                        }
                    return true
                }
            },
        )

        // Agregar scroll listener
        binding.RVViewUsers.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    // Guardar la posición del scroll
                    viewModel.updateScrollPosition(lastVisibleItem + 1)

                    if (totalItemCount <= (lastVisibleItem + 1)) {
                        viewModel.updateScrollPosition(layoutManager.findFirstVisibleItemPosition()) // Guardar posición aquí
                        // Si se llega al final, cargar más usuarios
                        viewModel.getUsuarios()
                    }
                }
            },
        )

        // Reemplazo explícito del fragmento con SettingsAdminFragment
        binding.btnBack.setOnClickListener {
            val settingsAdminFragment = SettingsFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, settingsAdminFragment)
                .addToBackStack(null)
                .commit()
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
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, RolAdministradorFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "Staff" -> {
                binding.btnStaff.setTextColor(Color.YELLOW)
                parentFragmentManager
                    .beginTransaction()
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
