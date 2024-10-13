// FAQDetailFragment.kt
package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqDetailBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

class FAQDetailFragment : Fragment() {
    private var _binding: FragmentFaqDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FAQViewModel by activityViewModels()
    lateinit var selectedFAQ: FAQBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqDetailBinding.inflate(inflater, container, false)
        setupBackButton()
        setupMoreVertButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                binding.preguntaTextView.text = it.Pregunta
                binding.respuestaTextView.text = it.Respuesta
                selectedFAQ = it

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.selectedFAQ.removeObservers(viewLifecycleOwner)
    }

    private fun setupBackButton() {
        binding.BRegresar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupMoreVertButton() {
        binding.BAlter.setOnClickListener {
            var selectedFAQ = selectedFAQ.IdPregunta
            if (selectedFAQ == null) {
            Log.d("FAQDetailFragment", "selectedFAQ es nulo, lo dejo así")
            }
            if (selectedFAQ != null) {
                Log.d("FAQDetailFragment", "MoreVert button clicked")
                Log.d("FAQDetailFragment", "IdPregunta: $selectedFAQ")
                val bundle = Bundle()
                bundle.putInt("IdPregunta", selectedFAQ)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, FAQModifyFragment().apply {
                        arguments = bundle
                    })
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.d("FAQDetailFragment", "selectedFAQ is null")
            }
        }
    }
}