// FAQDetailFragment.kt
package com.kotlin.sacalabici.framework.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentFaqDetailBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import com.kotlin.sacalabici.framework.views.fragments.FAQFragment

class FAQDetailFragment : Fragment() {
    private var _binding: FragmentFaqDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FAQViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqDetailBinding.inflate(inflater, container, false)
        setupBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                Log.d("FAQDetailFragment", "FAQ: $it")
                Log.d("FAQDetailFragment", "Pregunta: ${it.Pregunta}")
                Log.d("FAQDetailFragment", "Respuesta: ${it.Respuesta}")
                binding.preguntaTextView.text = it.Pregunta
                binding.respuestaTextView.text = it.Respuesta
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBackButton() {
        binding.BRegresar.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, FAQFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}