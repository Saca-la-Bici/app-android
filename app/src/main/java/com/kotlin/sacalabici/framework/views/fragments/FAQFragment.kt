package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.adapters.FAQAdapter
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import kotlinx.coroutines.launch

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private lateinit var adapter: FAQAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FAQViewModel
    private var permissions: List<String> = emptyList()

    private var faqList: ArrayList<FAQBase> = ArrayList()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[FAQViewModel::class.java]
        val root: View = binding.root
        setupBackButton()
        initializeComponents(root)
        viewModel.getFAQList()

        initializeObservers()
        setupRegisterFAQsButton()

        // Listener for search filter
        binding.etFilter.addTextChangedListener { query ->
            val filteredList = filterFAQs(query.toString())
            adapter.updateList(filteredList)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeObservers() {
        viewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            if (permissions.contains("Registrar pregunta frecuente")) {
                binding.BAgregarPregunta.visibility = View.VISIBLE
            }
        }
        // Observing the FAQ list data from the ViewModel
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqListData ->
            lifecycleScope.launch {
                setUpRecyclerView(ArrayList(faqListData))
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessageFAQ.text = errorMessage
        }

        viewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                Log.d("FAQFragment", "Selected FAQ: ${faq.IdPregunta}")
                // Navega solo si no está ya en el BackStack
                if (parentFragmentManager.findFragmentByTag("FAQDetailFragment") == null) {
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment_content_main, FAQDetailFragment(), "FAQDetailFragment")
                    transaction.addToBackStack(null)
                    transaction.commit()

                    // Limpia el valor de `selectedFAQ` para evitar que se dispare nuevamente al regresar
                    viewModel.selectedFAQ.postValue(null)
                }
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ)
        adapter = FAQAdapter(viewModel) // Initialize the adapter here
    }

    private fun setUpRecyclerView(dataForList: ArrayList<FAQBase>) {
        faqList = dataForList

        Log.d("FAQFragment", "FAQ List Size: ${dataForList.size}")
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        recyclerView.layoutManager = linearLayoutManager
        adapter.setFAQAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }

    // Function to handle back button, navigating to SettingsFragment
    private fun setupBackButton() {
        binding.BRegresar.setOnClickListener {
            viewModel.selectedFAQ.postValue(null)  // Limpiar el valor seleccionado
            parentFragmentManager.popBackStack()
        }
    }

    // Function to filter FAQs based on the search query
    private fun filterFAQs(query: String): ArrayList<FAQBase> =
        if (query.isEmpty()) {
            faqList // If query is empty, return the full list
        } else {
            // Filter the FAQ list based on the query
            val filteredList = ArrayList<FAQBase>()
            for (faq in faqList) {
                if (faq.Pregunta.contains(query, ignoreCase = true)) {
                    filteredList.add(faq)
                }
            }
            filteredList
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