package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.databinding.FragmentActivitiesBinding

class ActivitiesFragment: Fragment() {
    private var _binding: FragmentActivitiesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    private fun registerActivity() {
        val btnRegister = binding.fabAddActivity
        btnRegister.setOnClickListener {
            showActivityDialogue()
        }
    }

    /*
    * Muestra una ventana para que el usuario elija el tipo de actividad
    * a crear. Puede ser una rodada, un taller o un evento.
    * */
    private fun showActivityDialogue() {
        val options = arrayOf("Añadir rodada", "Añadir taller", "Añadir evento")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elige un tipo de actividad")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {

                }
                1 -> {

                }
                2 -> {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}