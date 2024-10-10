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
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsFragment
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val adapter: FAQAdapter = FAQAdapter()
    private lateinit var viewModel: FAQViewModel

    private var faqList: ArrayList<FAQBase> = ArrayList()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FAQViewModel::class.java]
        val root: View = binding.root
        setupBackButton()
        initializeComponents(root)
        viewModel.getFAQList()

        initializeObservers()

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
        // Observing the FAQ list data from the ViewModel
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqListData ->
            lifecycleScope.launch {
                delay(50)
                setUpRecyclerView(ArrayList(faqListData))
            }
        }

        // Observer for error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessageFAQ.text = errorMessage
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.recyclerFAQ)
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
        val btnFAQs = binding.BRegresar
        btnFAQs.setOnClickListener {
            // Navigate to SettingsFragment and replace content in MainActivity's container
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsFragment())
                .addToBackStack(null) // Allows navigating back
                .commit()
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
}
