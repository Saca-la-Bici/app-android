package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.network.preguntasFrecuentes.PreguntaFrecuenteProvider
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding
import com.kotlin.sacalabici.framework.PreguntaFrecuenteAdapter

class FAQsFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)

        // Inicializar el RecyclerView
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(context)
        binding.recyclerFAQ.layoutManager = manager
        binding.recyclerFAQ.adapter = PreguntaFrecuenteAdapter(PreguntaFrecuenteProvider.preguntaFrecuenteList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
