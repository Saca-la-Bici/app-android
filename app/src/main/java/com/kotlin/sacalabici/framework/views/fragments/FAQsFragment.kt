package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.databinding.FragmentFaqsBinding

class FAQsFragment : Fragment() {
    private var _binding: FragmentFaqsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout y obtener el binding
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)

        // Inicializar el RecyclerView
        // initRecyclerView()

        // Retorna la vista raíz del binding
        return binding.root
    }
    /*
    // Método para inicializar el RecyclerView
    private fun initRecyclerView() {
        val manager = LinearLayoutManager(context)
        binding.recyclerFAQ.layoutManager = manager
        binding.recyclerFAQ.adapter =
            FAQAdapter(FAQProvider.FAQList) { FAQ ->
                onItemSelected(FAQ)
            }
    }
*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
