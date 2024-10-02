package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.databinding.FragmentEventosBinding
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventosFragment : Fragment() {

    private var _binding: FragmentEventosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ActivitiesAdapter
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()
        setupObservers()
        setupSwipeRefreshLayout()
        loadInitialData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeComponents() {
        binding.errorMessageEventos.visibility = View.GONE
        binding.recyclerViewEventos.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActivitiesAdapter(mutableListOf()) { evento ->
            true
        }
        binding.recyclerViewEventos.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchEventosWithDelay()
        }
    }

    private fun setupObservers() {
        activitiesViewModel.eventosLiveData.observe(viewLifecycleOwner) { eventos ->
            adapter.updateData(eventos)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        activitiesViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.errorMessageEventos.text = errorMessage
                binding.errorMessageEventos.visibility = View.VISIBLE
            } else {
                binding.errorMessageEventos.visibility = View.GONE
            }
        }
    }

    private fun loadInitialData() {
        activitiesViewModel.getEventos()
    }

    private fun fetchEventosWithDelay() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(50)
            activitiesViewModel.getEventos()
        }
    }
}