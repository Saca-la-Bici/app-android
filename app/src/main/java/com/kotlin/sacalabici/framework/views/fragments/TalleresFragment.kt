package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.adapters.ActivitiesAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class TalleresFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorMessageTextView: TextView
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_talleres, container, false)

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewTalleres)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar adapter aquí, donde el fragmento ya está adjunto a su contexto
        adapter = ActivitiesAdapter(ArrayList()) { taller ->
            true
        }
        recyclerView.adapter = adapter

        // Inicializar SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            activitiesViewModel.getTalleres()  // Obtener los datos más recientes
        }

        // Inicializar TextView para mensaje de error
        errorMessageTextView = view.findViewById(R.id.errorMessageTalleres)

        // Observar cambios en talleres
        activitiesViewModel.talleresLiveData.observe(viewLifecycleOwner) { talleres ->
            adapter.updateData(talleres)  // Método para actualizar los datos del adapter
            swipeRefreshLayout.isRefreshing = false  // Detener la animación
        }

        // Observar cambios en errorMessageLiveData
        activitiesViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                errorMessageTextView.text = errorMessage
                errorMessageTextView.visibility = View.VISIBLE
            } else {
                errorMessageTextView.visibility = View.GONE
            }
        }

        // Cargar los datos iniciales
        activitiesViewModel.getTalleres()

        return view
    }
}