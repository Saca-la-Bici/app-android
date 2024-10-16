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
import com.kotlin.sacalabici.databinding.FragmentTalleresBinding
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TalleresFragment : Fragment() {

    private var _binding: FragmentTalleresBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ActivitiesAdapter
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTalleresBinding.inflate(inflater, container, false)
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
        binding.errorMessageTalleres.visibility = View.GONE
        binding.recyclerViewTalleres.layoutManager = LinearLayoutManager(requireContext())

        adapter = ActivitiesAdapter(mutableListOf(), { taller ->
            passDetailsActivity(taller.id)
        }, activitiesViewModel)

        binding.recyclerViewTalleres.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchTalleresWithDelay()
        }
    }

    private fun setupObservers() {
        activitiesViewModel.talleresLiveData.observe(viewLifecycleOwner) { talleres ->
            if (talleres.isNotEmpty()) {
                adapter.updateData(talleres)
                binding.errorMessageTalleres.visibility = View.GONE
            } else {
                binding.errorMessageTalleres.visibility = View.VISIBLE
            }

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadInitialData() {
        activitiesViewModel.getTalleres()
    }

    private fun fetchTalleresWithDelay() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(50)
            activitiesViewModel.getTalleres()
        }
    }

    // Iniciar activity con detalles acorde al ID del taller seleccionado
    private fun passDetailsActivity(tallerId: String){
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply {
            putExtra("ACTIVITY_ID", tallerId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}