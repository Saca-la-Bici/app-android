package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.databinding.FragmentEventBinding
import com.kotlin.sacalabici.framework.adapters.ProfileAdapter
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class EventFragment : Fragment() {
    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProfileAdapter
    private lateinit var profileViewModel: ProfileViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
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
        binding.errorMessageEvent.visibility = View.GONE
        binding.recyclerViewProfileActivities.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProfileAdapter(mutableListOf(), { event ->
            passDetailsActivity(event.id)
        }, profileViewModel)

        binding.recyclerViewProfileActivities.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchEventWithDelay()
        }
    }

    private fun setupObservers() {
        profileViewModel.eventLiveData.observe(viewLifecycleOwner) { event ->
            adapter.updateData(event)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        profileViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.errorMessageEvent.text = errorMessage
                binding.errorMessageEvent.visibility = View.VISIBLE
            } else {
                binding.errorMessageEvent.visibility = View.GONE
            }
        }
    }

    private fun loadInitialData() {
        profileViewModel.getEventos()
    }

    private fun fetchEventWithDelay() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(50)
            profileViewModel.getEventos()
        }
    }

    private fun passDetailsActivity(eventId: String){
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply{
            putExtra("ACTIVITY_ID", eventId)
        }
        startActivity(intent)
    }
}
