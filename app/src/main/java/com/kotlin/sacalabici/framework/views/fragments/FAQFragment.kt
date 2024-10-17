package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.adapters.FAQAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsFragment
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import kotlinx.coroutines.launch

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private lateinit var adapter: FAQAdapter
    private lateinit var recyclerView: RecyclerView
    private val faqViewModel: FAQViewModel by activityViewModels()
    private var permissions: List<String> = emptyList()

    private var faqList: ArrayList<FAQBase> = ArrayList()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupBackButton()
        initializeComponents(root)
        faqViewModel.getFAQList()
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

    override fun onResume() {
        super.onResume()
        faqViewModel.getFAQList()
    }

    private fun initializeObservers() {
        faqViewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            if (permissions.contains("Modificar pregunta frecuente")) {
                binding.BAgregarPregunta.visibility = View.VISIBLE
            } else {
                binding.BAgregarPregunta.visibility = View.GONE
            }
        }

        // Observing the FAQ list data from the ViewModel
        faqViewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqListData ->
            lifecycleScope.launch {
                setUpRecyclerView(ArrayList(faqListData))
                adapter.notifyDataSetChanged()
            }
        }

        faqViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessageFAQ.text = errorMessage
        }

        faqViewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                Log.d("FAQFragment", "Selected FAQ: ${faq.IdPregunta}")
                if (parentFragmentManager.findFragmentByTag("FAQDetailFragment") == null) {
                    val transaction = parentFragmentManager.beginTransaction()
                    val fragment =
                        FAQDetailFragment().apply {
                            arguments =
                                Bundle().apply {
                                    putSerializable("selectedFAQ", faq)
                                }
                        }
                    transaction.replace(R.id.nav_host_fragment_content_main, fragment, "FAQDetailFragment")
                    transaction.addToBackStack(null)
                    transaction.commit()
                    faqViewModel.selectedFAQ.postValue(null)
                }
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ)
        adapter = FAQAdapter(faqViewModel) // Initialize the adapter here
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
            faqViewModel.selectedFAQ.postValue(null) // Limpiar el valor seleccionado
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // Function to filter FAQs based on the search query
    private fun filterFAQs(query: String): ArrayList<FAQBase> {
        val filteredList: ArrayList<FAQBase> =
            if (query.isEmpty()) {
                faqList // If query is empty, return the full list
            } else {
                // Filter the FAQ list based on the query
                val tempFilteredList = ArrayList<FAQBase>()
                for (faq in faqList) {
                    if (faq.Pregunta.contains(query, ignoreCase = true)) {
                        tempFilteredList.add(faq)
                    }
                }
                tempFilteredList
            }

        // Show no results message
        if (filteredList.isEmpty()) {
            binding.errorMessageFAQ.visibility = View.VISIBLE
            binding.errorMessageFAQ.text = "No se encontraron preguntas frecuentes."
        } else {
            binding.errorMessageFAQ.visibility = View.GONE
        }
        return filteredList
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
