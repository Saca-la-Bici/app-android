package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.adapters.FAQAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsAdminFragment
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FAQAdapter
    private lateinit var viewModel: FAQViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflar el layout del fragmento
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FAQViewModel::class.java]
        val root: View = binding.root

        // Inicializar componentes del layout
        initializeComponents(root)
        setupRegresarButton()

        // Inicializar observers para escuchar cambios en LiveData
        initializeObservers()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Liberar el binding para evitar memory leaks
    }

    // Inicializar RecyclerView y otros componentes
    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)

        adapter =
            FAQAdapter(ArrayList()) { faq ->
                true
            }

        recyclerView.adapter = adapter
    }

    // Observa los cambios en los datos de las FAQs y actualiza el RecyclerView
    private fun initializeObservers() {
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqList ->
            lifecycleScope.launch {
                delay(50) // Espera breve para asegurar la actualización de la UI
                updateRecyclerView(ArrayList(faqList)) // Actualiza el RecyclerView con las FAQs
            }
        }
    }

    // Actualiza el adaptador del RecyclerView con la nueva lista de FAQs
    private fun updateRecyclerView(dataForList: ArrayList<FAQBase>) {
        adapter.updateData(dataForList)
    }

    // Configura el botón "Regresar" para navegar al fragmento de SettingsAdminFragment
    private fun setupRegresarButton() {
        val btnFAQs = binding.BRegresar
        btnFAQs.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsAdminFragment())
                .addToBackStack(null) // Añadir la transacción al back stack para navegación hacia atrás
                .commit()
        }
    }
}
