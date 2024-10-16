package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
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
    private lateinit var selectedFAQ: FAQBase
    private var permissions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqDetailBinding.inflate(inflater, container, false)

        // Obtener la FAQ seleccionada
        selectedFAQ = arguments?.getSerializable("selectedFAQ") as FAQBase
        binding.preguntaTextView.text = selectedFAQ.Pregunta
        binding.respuestaTextView.text = selectedFAQ.Respuesta

        // Inicializar observadores
        initializeObservers()

        // Configurar botón de regresar
        binding.BRegresar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Configurar botón de modificar
        binding.BAlter.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putSerializable("faqToEdit", selectedFAQ)
                    putString("temaToEdit", selectedFAQ.Tema)
                }
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.nav_host_fragment_content_main,
                    FAQModifyFragment().apply { arguments = bundle },
                ).addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun initializeObservers() {
        viewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            if (permissions.contains("Modificar pregunta frecuente")) {
                binding.BAlter.visibility = View.VISIBLE
            }
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
