package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.FragmentFaqModifyBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

class FAQModifyFragment : Fragment() {
    private val viewModel: FAQViewModel by activityViewModels()
    private var _binding: FragmentFaqModifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var faq: FAQBase
    private var permissions: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFaqModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        faq = arguments?.getSerializable("faqToEdit") as FAQBase
        val tema = arguments?.getString("temaToEdit")

        binding.preguntaEditText.setText(faq.Pregunta)
        binding.respuestaEditText.setText(faq.Respuesta)
        setTemaRadioButton(tema)

        viewModel.permissionsLiveData.observe(viewLifecycleOwner) { permissions ->
            this.permissions = permissions
            Log.d("FAQModifyFragment", "Permissions: $permissions")
            if (permissions.contains("Eliminar pregunta frecuente")) {
                binding.BEliminar.visibility = View.VISIBLE
            }
        }

        binding.BCancelar.setOnClickListener {
            val alertDialog =
                AlertDialog
                    .Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas cancelar los cambios?")
                    .setPositiveButton("Sí") { _, _ ->
                        parentFragmentManager.popBackStack()
                    }.setNegativeButton("No", null)
                    .create()

            alertDialog.show()
        }

        binding.BEliminar.setOnClickListener {
            val alertDialog =
                AlertDialog
                    .Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta pregunta?")
                    .setPositiveButton("Sí") { _, _ ->
                        Log.d("FAQModifyFragment", "Delete button confirmed")
                        deleteFAQ(faq.IdPregunta)
                    }.setNegativeButton("No", null)
                    .create()

            alertDialog.show()
        }

        binding.BConfirmar.setOnClickListener {
            val newPregunta = binding.preguntaEditText.text.toString()
            val newRespuesta = binding.respuestaEditText.text.toString()
            val newTema = getSelectedTema()

            if (newPregunta == faq.Pregunta && newRespuesta == faq.Respuesta && newTema == faq.Tema) {
                Snackbar.make(binding.root, "No se hicieron modificaciones", Snackbar.LENGTH_SHORT).show()
            } else {
                val alertDialog =
                    AlertDialog
                        .Builder(requireContext())
                        .setTitle("Confirmación")
                        .setMessage("¿Estás seguro de que deseas guardar los cambios?")
                        .setPositiveButton("Sí") { _, _ ->
                            faq.Pregunta = newPregunta
                            faq.Respuesta = newRespuesta
                            faq.Tema = newTema
                            viewModel.modifyFAQ(faq)
                            parentFragmentManager.popBackStack()
                        }.setNegativeButton("No", null)
                        .create()

                alertDialog.show()
            }
        }
    }

    private fun setTemaRadioButton(tema: String?) {
        when (tema) {
            getString(R.string.Categoria1FAQ) -> binding.radioGroup.check(R.id.categoria1)
            getString(R.string.Categoria2FAQ) -> binding.radioGroup.check(R.id.categoria2)
            getString(R.string.Categoria3FAQ) -> binding.radioGroup.check(R.id.categoria3)
            getString(R.string.Categoria4FAQ) -> binding.radioGroup.check(R.id.categoria4)
            getString(R.string.Categoria5FAQ) -> binding.radioGroup.check(R.id.categoria5)
        }
    }

    private fun getSelectedTema(): String =
        when (binding.radioGroup.checkedRadioButtonId) {
            R.id.categoria1 -> getString(R.string.Categoria1FAQ)
            R.id.categoria2 -> getString(R.string.Categoria2FAQ)
            R.id.categoria3 -> getString(R.string.Categoria3FAQ)
            R.id.categoria4 -> getString(R.string.Categoria4FAQ)
            R.id.categoria5 -> getString(R.string.Categoria5FAQ)
            else -> ""
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
