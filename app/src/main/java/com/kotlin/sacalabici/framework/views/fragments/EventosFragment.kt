package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
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
import com.kotlin.sacalabici.framework.views.activities.activities.DetailsActivity
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

        adapter = ActivitiesAdapter(mutableListOf(), { evento ->
            passDetailsActivity(evento.id)
        }, activitiesViewModel)

        binding.recyclerViewEventos.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchEventosWithDelay()
        }
    }

    private fun setupObservers() {
        activitiesViewModel.eventosLiveData.observe(viewLifecycleOwner) { eventos ->
            if (eventos.isNotEmpty()) {
                adapter.updateData(eventos)
                binding.errorMessageEventos.visibility = View.GONE
            } else {
                adapter.updateData(eventos)
                binding.errorMessageEventos.visibility = View.VISIBLE
            }
            binding.swipeRefreshLayout.isRefreshing = false
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

    // Iniciar ectivity con detalles acorde al ID del evento seleccionado
    private fun passDetailsActivity(eventoId: String) {
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply {
            putExtra("ACTIVITY_ID", eventoId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}