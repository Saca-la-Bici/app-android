package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.databinding.FragmentActivitiesBinding
import com.kotlin.sacalabici.framework.adapters.views.activities.DetailActivity

class ActivitiesFragment : Fragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Listener para el botón que abre una nueva Activity
        binding.navigateButton.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
