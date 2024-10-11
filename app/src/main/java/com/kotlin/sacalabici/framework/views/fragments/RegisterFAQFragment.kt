package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentRegisterFaqBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

class RegisterFAQFragment : Fragment() {
    private var _binding: FragmentRegisterFaqBinding? = null
    private val binding get() = _binding!!

    // Instancia del ViewModel
    private val faqViewModel: FAQViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterFaqBinding.inflate(inflater, container, false)

        // Configurar el botón de cerrar
        setupCloseButton()

        // Configurar el botón para registrar la pregunta
        setupSubmitButton()

        // Escuchar los posibles mensajes de error del ViewModel
        observeViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCloseButton() {
        val btnClose = binding.BCerrar
        btnClose.setOnClickListener {
            // Navegar a FAQFragment
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, FAQFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSubmitButton() {
        val btnSubmit = binding.iconAceptar
        btnSubmit.setOnClickListener {
            // Recoger los datos del usuario
            val pregunta = binding.tituloPreguntaCuadro.text.toString()
            val respuesta = binding.descripcionPreguntaCuadro.text.toString()
            val tema = getSelectedTema()

            // Validar que los campos no estén vacíos
            if (pregunta.isNotBlank() && respuesta.isNotBlank() && tema != null) {
                // Hacer la llamada al ViewModel para registrar la pregunta
                faqViewModel.postFAQ(pregunta, respuesta, tema, "")

                // Mostrar mensaje de éxito
                Toast.makeText(requireContext(), "Pregunta registrada con éxito", Toast.LENGTH_SHORT).show()

                // Navegar de vuelta a FAQFragment tras registrar la pregunta
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, FAQFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                // Mostrar mensaje de error si faltan datos
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener el tema seleccionado
    private fun getSelectedTema(): String? {
        val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId != -1) {
            val selectedRadioButton: RadioButton = binding.radioGroup.findViewById(selectedRadioButtonId)
            return selectedRadioButton.text.toString()
        }
        return null
    }

    // Observar los mensajes de error del ViewModel
    private fun observeViewModel() {
        faqViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // Mostrar mensaje de error si hay un problema al registrar la pregunta
                Toast.makeText(requireContext(), "Error al registrar la pregunta frecuente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
