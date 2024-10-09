// FAQFragment.kt
package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.adapters.FAQAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsFragment
import com.kotlin.sacalabici.framework.ui.FAQDetailFragment
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FAQFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private lateinit var adapter: FAQAdapter
    private lateinit var viewModel: FAQViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[FAQViewModel::class.java]
        setupBackButton()
        initializeComponents()
        viewModel.getFAQList()
        initializeObservers()
        return binding.root
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

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessageFAQ.text = errorMessage
        }

        viewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                // Navigate to FAQDetailFragment programmatically
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment_content_main, FAQDetailFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    private fun initializeComponents() {
        binding.recyclerFAQ.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpRecyclerView(dataForList: ArrayList<FAQBase>) {
        adapter = FAQAdapter(viewModel)
        adapter.setFAQAdapter(dataForList, requireContext())
        binding.recyclerFAQ.adapter = adapter
    }

    private fun setupBackButton() {
        binding.BRegresar.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}