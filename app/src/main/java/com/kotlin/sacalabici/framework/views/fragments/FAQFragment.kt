package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.FAQAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsAdminFragment
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    val binding get() = _binding!!

    private lateinit var viewModel: FAQViewModel
    private lateinit var recyclerView: RecyclerView
    private val adapter: FAQAdapter = FAQAdapter()
    private lateinit var data: ArrayList<FAQBase>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[FAQViewModel::class.java]

        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Función para regresar a SettingsFragment
        binding.BRegresar.setOnClickListener {
            val SettingsAdminFragment = SettingsAdminFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsAdminFragment)
                .addToBackStack(null)
                .commit()
        }

        // Botón para abrir RegisterFAQFragment
        setupRegisterFAQsButton()

        data = ArrayList()

        // Llamada a métodos para inicializar componentes y observar cambios en el ViewModel
        initializeComponents(root)
        initializeObservers()
        viewModel.getFAQList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Método para inicializar los componentes de la interfaz
    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ) // Vincula el RecyclerView
    }

    // Método para inicializar los observadores de LiveData en el ViewModel
    private fun initializeObservers() {
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { pokedexObject ->
            setUpRecyclerView(pokedexObject.results)
        }
    }

    // Método para configurar el RecyclerView
    private fun setUpRecyclerView(dataForList: ArrayList<FAQBase>) {
        recyclerView.setHasFixedSize(true) // Fija el tamaño del RecyclerView para optimización

        val linearLayoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        recyclerView.layoutManager = linearLayoutManager

        // Configura el adapter para gestionar los datos
        adapter.FAQAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }

    // Función para que el botón de Agregar FAQ de lleve a RegisterFAQFragment
    private fun setupRegisterFAQsButton() {
        val btnFAQs = binding.BAgregarPregunta
        btnFAQs.setOnClickListener {
            // Navegar a RegisterFAQsFragment y reemplazar el contenido en el contenedor principal de MainActivity
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, RegisterFAQFragment())
                .addToBackStack(null) // Para permitir navegar hacia atrás
                .commit()
        }
    }
}
