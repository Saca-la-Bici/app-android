package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.FragmentTotalMedalsBinding
import com.kotlin.sacalabici.framework.adapters.TotalMedalsAdapter
import com.kotlin.sacalabici.framework.viewmodel.TotalMedalsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TotalMedalsFragment : Fragment() {
    private var _binding: FragmentTotalMedalsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val adapter: TotalMedalsAdapter = TotalMedalsAdapter()
    private lateinit var viewModel: TotalMedalsViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTotalMedalsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TotalMedalsViewModel::class.java]
        val root: View = binding.root
        initializeComponents(root)
        viewModel.getMedalsList()

        initializeObservers()

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeObservers() {
        viewModel.medalsObjectLiveData.observe(viewLifecycleOwner) { medalsList ->
            medalsList?.let{
                lifecycleScope.launch {
                    delay(50)
                    setUpRecyclerView(ArrayList(medalsList))
                }
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVMedallas)
    }

    private fun setUpRecyclerView(dataForList: ArrayList<MedalBase>) {
        recyclerView.setHasFixedSize(true)

        val gridLayoutManager =
            GridLayoutManager(
                requireContext(),
                3, // 3 columnas
                GridLayoutManager.VERTICAL, // Disposición vertical
                false,
            )

        recyclerView.layoutManager = gridLayoutManager

        adapter.setTotalMedalsAdapter(dataForList, requireContext())

        recyclerView.adapter = adapter
    }
}
