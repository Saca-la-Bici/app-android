package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.data.models.RutasObject
import com.kotlin.sacalabici.databinding.FragmentListaRutasBinding
import com.kotlin.sacalabici.framework.adapters.RutasAdapter
import com.kotlin.sacalabici.framework.viewmodel.RutasViewModel

class RutasFragment : Fragment() {
    private var _binding: FragmentListaRutasBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: RutasViewModel

    private lateinit var recyclerView: RecyclerView
    private val adapter: RutasAdapter = RutasAdapter()
    private lateinit var data: ArrayList<RutasBase>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[RutasViewModel::class.java]

        _binding = FragmentListaRutasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        data = ArrayList()

        initializeComponents(root)
        initializeObservers()
        viewModel.getRutasList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVRutas)
    }

    private fun initializeObservers() {
        viewModel.rutasObjectLiveData.observe(viewLifecycleOwner) { RutasObject ->
            setUpRecyclerView(RutasObject.rutas)
        }
    }

    private fun setUpRecyclerView(dataForList: ArrayList<RutasBase>) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.layoutManager = linearLayoutManager
        adapter.RutasAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }
}
