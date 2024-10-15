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
    private var permissions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedFAQ = arguments?.getSerializable("selectedFAQ") as FAQBase
        binding.preguntaTextView.text = selectedFAQ.Pregunta
        binding.respuestaTextView.text = selectedFAQ.Respuesta

        viewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            Log.d("FAQDetailFragment", "Permissions: $permissions")
            if (permissions.contains("Modificar pregunta frecuente")) {
                binding.BAlter.visibility = View.VISIBLE
            }
        }

        binding.BRegresar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.BAlter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("faqToEdit", selectedFAQ)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, FAQModifyFragment().apply {
                    arguments = bundle
                })
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar los datos de la pregunta frecuente
        binding.preguntaTextView.text = selectedFAQ.Pregunta
        binding.respuestaTextView.text = selectedFAQ.Respuesta
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}