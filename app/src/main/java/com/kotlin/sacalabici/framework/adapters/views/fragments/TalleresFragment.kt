package com.kotlin.sacalabici.framework.adapters.views.fragments

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
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel

class TalleresFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_talleres, container, false)

        // Inicializar adapter aquí, donde el fragmento ya está adjunto a su contexto
        adapter = ActivitiesAdapter(ArrayList(), requireContext()) { taller ->
            true
        }

        recyclerView = view.findViewById(R.id.recyclerViewTalleres)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Inicializar SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            activitiesViewModel.getTalleres()  // Obtener los datos más recientes
        }

        // Observar cambios en talleres
        activitiesViewModel.rodadasLiveData.observe(viewLifecycleOwner) { talleres ->
            adapter.updateData(talleres)  // Método para actualizar los datos del adapter
            swipeRefreshLayout.isRefreshing = false  // Detener la animación de refresco
        }

        // Cargar los datos iniciales
        activitiesViewModel.getTalleres()

        return view
    }
}