package com.kotlin.sacalabici.framework.views.fragments

import TotalMedalsViewModel
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.FragmentTotalMedalsBinding
import com.kotlin.sacalabici.framework.adapters.TotalMedalsAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.ProfileFragment
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTotalMedalsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TotalMedalsViewModel::class.java]
        val root: View = binding.root

        initializeComponents(root)
        setFragmentResultListener("actionButtonDialogResult") { _, bundle ->
            val resultCode = bundle.getInt("resultCode")
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getMedalsList()
            }
        }
        initializeObservers()
        setupRegresarButton()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeObservers() {
        viewModel.medalsObjectLiveData.observe(viewLifecycleOwner) { medalsList ->
            lifecycleScope.launch {
                setUpRecyclerView(ArrayList(medalsList))
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVMedallas)

    }

    private fun setUpRecyclerView(dataForList: ArrayList<MedalBase>) {
        recyclerView.setHasFixedSize(true)

        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            3,  // 3 columnas
            GridLayoutManager.VERTICAL,  // Disposición vertical
            false
        )

        recyclerView.layoutManager = gridLayoutManager

        adapter.TotalMedalsAdapter(dataForList, requireContext())

        recyclerView.adapter = adapter
    }

    //Función para que el botón de Regresar de lleve a SettingsFragment
    private fun setupRegresarButton() {
        val btnFAQs = binding.BRegresar
        btnFAQs.setOnClickListener {
            // Navegar a SettingFragment y reemplazar el contenido en el contenedor principal de MainActivity
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, ProfileFragment())
                .addToBackStack(null) // Para permitir navegar hacia atrás
                .commit()
        }
    }

}
