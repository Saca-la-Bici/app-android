// FAQDetailFragment.kt
package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kotlin.sacalabici.databinding.FragmentFaqDetailBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

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
                binding.preguntaTextView.text = it.Pregunta
                binding.respuestaTextView.text = it.Respuesta
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Elimina el observador de la FAQ seleccionada para evitar navegaciones inesperadas
        viewModel.selectedFAQ.removeObservers(viewLifecycleOwner)
    }


    private fun setupBackButton() {
        binding.BRegresar.setOnClickListener {
            parentFragmentManager.popBackStack()
            // Limpiar el valor seleccionado al regresar
            viewModel.selectedFAQ.postValue(null)
        }
    }


}