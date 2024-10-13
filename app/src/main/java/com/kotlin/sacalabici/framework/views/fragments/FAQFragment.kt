package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.adapters.FAQAdapter
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
        initializeObservers()
        initializeComponents()
        viewModel.getFAQList()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeObservers() {
        viewModel.faqObjectLiveData.observe(viewLifecycleOwner) { faqList ->
            lifecycleScope.launch {
                _binding?.let {
                    setUpRecyclerView(ArrayList(faqList))
                }
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
            viewModel.selectedFAQ.postValue(null)  // Limpiar el valor seleccionado
            parentFragmentManager.popBackStack()
        }
    }

}