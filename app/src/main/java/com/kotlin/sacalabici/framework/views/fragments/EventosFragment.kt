package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.FragmentEventosBinding
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EventosFragment : Fragment() {

    private var _binding: FragmentEventosBinding? = null
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
        }, { evento ->
            showDialog(evento)
        } , activitiesViewModel)

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

        activitiesViewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            savePermissionsToSharedPreferences(permissions)
        }
    }

    private fun loadInitialData() {
        activitiesViewModel.getEventos()
        activitiesViewModel.getPermissions()
    }

    private fun fetchEventosWithDelay() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(50)
            activitiesViewModel.getEventos()
        }
    }

    // Iniciar activity con detalles acorde al ID del evento seleccionado
    private fun passDetailsActivity(eventoId: String){
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply{
            putExtra("ACTIVITY_ID", eventoId)
        }
        startActivity(intent)
    }

    private fun showDialog(evento: Activity) {
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(evento.date)

        // Convertir lista de usuarios en ArrayList<String>
        val registerArrayList = ArrayList<String>()
        evento.register?.let { registerArrayList.addAll(it) }

        Log.d("EventosFragment", evento.toString())

        if (storedPermissions?.contains("Modificar actividad") == true) {
            val dialogFragment = ActivityActionDialogFragment.newInstance(
                evento.id,
                evento.title,
                formattedDate,
                evento.time,
                evento.location,
                evento.description,
                evento.duration,
                evento.imageURL,
                "Evento",
                evento.peopleEnrolled,
                evento.state,
                evento.foro,
                registerArrayList,
                null,
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