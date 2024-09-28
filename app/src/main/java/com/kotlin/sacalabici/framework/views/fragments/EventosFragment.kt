package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class EventosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_eventos, container, false)

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEventos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar adapter aquí, donde el fragmento ya está adjunto a su contexto
        adapter = ActivitiesAdapter(ArrayList()) { evento ->
            true
        }
        recyclerView.adapter = adapter

        // Inicializar SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            activitiesViewModel.getEventos()  // Obtener los datos más recientes
        }

        // Observar cambios en eventos
        activitiesViewModel.eventosLiveData.observe(viewLifecycleOwner) { eventos ->
            adapter.updateData(eventos)  // Método para actualizar los datos del adapter
            swipeRefreshLayout.isRefreshing = false  // Detener la animación
        }

        // Cargar los datos iniciales
        activitiesViewModel.getEventos()

        return view
    }
}