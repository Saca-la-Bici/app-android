// FAQModifyFragment.kt
package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import android.util.Log
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqModifyBinding

class FAQModifyFragment : Fragment() {
    private val viewModel: FAQViewModel by activityViewModels()
    private var _binding: FragmentFaqModifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var faq: FAQBase
    private var permissions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        faq = arguments?.getSerializable("faqToEdit") as FAQBase

        binding.preguntaEditText.setText(faq.Pregunta)
        binding.respuestaEditText.setText(faq.Respuesta)

        viewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            Log.d("FAQModifyFragment", "Permissions: $permissions")
            if (permissions.contains("Eliminar pregunta frecuente")) {
                binding.BEliminar.visibility = View.VISIBLE
            }
        }

        binding.BCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.BEliminar.setOnClickListener {
            Log.d("FAQModifyFragment", "Delete button clicked")
            deleteFAQ(faq.IdPregunta)
        }

        binding.BConfirmar.setOnClickListener {
            faq.Pregunta = binding.preguntaEditText.text.toString()
            faq.Respuesta = binding.respuestaEditText.text.toString()
            parentFragmentManager.popBackStack()
        }
    }

    private fun deleteFAQ(IdPregunta: Int) {
        Log.d("FAQModifyFragment", "Calling deleteFAQ with Id: $IdPregunta")
        viewModel.deleteFAQ(IdPregunta)
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                Log.d("FAQModifyFragment", "Pregunta frecuente eliminada correctamente")
                parentFragmentManager.popBackStack()
                parentFragmentManager.popBackStack()
            } else {
                Log.e("FAQModifyFragment", "Error: $error")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}