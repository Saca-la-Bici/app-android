package com.kotlin.sacalabici.framework.views.fragments

import TotalMedalsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.FragmentTotalMedalsBinding
import com.kotlin.sacalabici.framework.adapters.TotalMedalsAdapter
import kotlinx.coroutines.launch

class TotalMedalsFragment : Fragment() {
    private var _binding: FragmentTotalMedalsBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: TotalMedalsViewModel

    private lateinit var recyclerView: RecyclerView

    private val adapter: TotalMedalsAdapter = TotalMedalsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTotalMedalsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TotalMedalsViewModel::class.java]
        val root: View = binding.root

        initializeComponents(root)
        initializeObservers()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // CAMBIAR
    private fun initializeObservers() {
        viewModel.medalsObjectLiveData.observe(viewLifecycleOwner) { ? ->
            lifecycleScope.launch {
                setUpRecyclerView(ArrayList(?))
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVMedallas)

    }


    private fun setUpRecyclerView(dataForList: ArrayList<MedalBase>) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.layoutManager = linearLayoutManager
        adapter.TotalMedalsAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }

}
