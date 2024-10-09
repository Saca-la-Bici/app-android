package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.FragmentRodadasBinding
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RodadasFragment: Fragment() {

    private var _binding: FragmentRodadasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ActivitiesAdapter
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRodadasBinding.inflate(inflater, container, false)
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
        binding.errorMessageRodadas.visibility = View.GONE
        binding.recyclerViewRodadas.layoutManager = LinearLayoutManager(requireContext())

        adapter = ActivitiesAdapter(mutableListOf(), { rodada ->
            passDetailsActivity(rodada.id)
        }, { rodada ->
            showDialog(rodada)
        }, activitiesViewModel)

        binding.recyclerViewRodadas.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchRodadasWithDelay()
        }
    }

    private fun setupObservers() {
        activitiesViewModel.rodadasLiveData.observe(viewLifecycleOwner) { rodadas ->
            adapter.updateData(rodadas)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        activitiesViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.errorMessageRodadas.text = errorMessage
                binding.errorMessageRodadas.visibility = View.VISIBLE
            } else {
                binding.errorMessageRodadas.visibility = View.GONE
            }
        }
    }

    private fun loadInitialData() {
        activitiesViewModel.getRodadas()
    }

    private fun fetchRodadasWithDelay() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(50)
            activitiesViewModel.getRodadas()
        }
    }

    private fun passDetailsActivity(rodadaId: String){
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply{
            putExtra("ACTIVITY_ID", rodadaId)
        }
        startActivity(intent)
    }

    private fun showDialog(rodada: Activity) {
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rodada.date)

        if (storedPermissions?.contains("Modificar actividad") == true) {
            val dialogFragment = ActivityActionDialogFragment.newInstance(
                rodada.id,
                rodada.title,
                formattedDate,
                rodada.time,
                rodada.location,
                rodada.description,
                rodada.duration,
                rodada.imageURL,
                "Rodada",
                storedPermissions
            )
            dialogFragment.show(parentFragmentManager, ActivityActionDialogFragment.TAG)
        }
    }

    private fun savePermissionsToSharedPreferences(permissions: List<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet("permissions", permissions.toSet())
        editor.apply()
    }
}
