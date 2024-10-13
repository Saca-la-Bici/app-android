package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel
import android.util.Log

class FAQModifyFragment : Fragment() {
    private val viewModel: FAQViewModel by activityViewModels()
    private var idPregunta: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_faq_modify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idPregunta = arguments?.getInt("IdPregunta")

        val preguntaEditText: EditText = view.findViewById(R.id.preguntaEditText)
        val respuestaEditText: EditText = view.findViewById(R.id.respuestaEditText)
        val cancelarButton: ImageButton = view.findViewById(R.id.BCancelar)
        val eliminarButton: Button = view.findViewById(R.id.BEliminar)

        viewModel.selectedFAQ.observe(viewLifecycleOwner) { faq ->
            faq?.let {
                preguntaEditText.setText(it.Pregunta)
                respuestaEditText.setText(it.Respuesta)
            }
        }

        cancelarButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        eliminarButton.setOnClickListener {
            Log.d("FAQModifyFragment", "Delete button clicked")
            idPregunta?.let { id ->
                deleteFAQ(id)
                Log.d("FAQModifyFragment", "Calling deleteFAQ with Id: $id")
            }
        }
    }

    private fun deleteFAQ(IdPregunta: Int) {
        Log.d("FAQModifyFragment", "Calling deleteFAQ with Id: $IdPregunta")
        viewModel.deleteFAQ(IdPregunta)
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                Log.d("FAQModifyFragment", "Pregunta frecuente eliminada correctamente")
                parentFragmentManager.popBackStack()
            } else {
                Log.e("FAQModifyFragment", "Error: $error")
            }
        }
    }
}