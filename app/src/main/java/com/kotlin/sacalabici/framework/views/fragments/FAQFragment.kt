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
import com.kotlin.sacalabici.framework.FAQAdapter
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
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FAQViewModel::class.java]
        val root: View = binding.root
        setupRegresarButton()
        initializeComponents(root)
        initializeObservers()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeObservers() {
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqList ->
            lifecycleScope.launch {
                delay(50)
                setUpRecyclerView(ArrayList(faqList))
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ)
    }

    private fun setUpRecyclerView(dataForList: ArrayList<FAQBase>) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        recyclerView.layoutManager = linearLayoutManager
        adapter.FAQAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }

    // Función para que el botón de Regresar de lleve a SettingsFragment
    private fun setupRegresarButton() {
        val btnFAQs = binding.BRegresar
        btnFAQs.setOnClickListener {
            // Navegar a SettingFragment y reemplazar el contenido en el contenedor principal de MainActivity
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsAdminFragment())
                .addToBackStack(null) // Para permitir navegar hacia atrás
                .commit()
        }
    }
}
