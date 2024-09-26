package com.kotlin.sacalabici.framework.views.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.databinding.FragmentActivitiesBinding
import com.kotlin.sacalabici.framework.views.activities.AddActivityActivity

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

        addActivity()
        return root
    }

    private fun addActivity() {
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

        // Crear diálogo
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elige un tipo de actividad")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Rodada")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Taller")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(requireContext(), AddActivityActivity::class.java)
                    intent.putExtra("type", "Evento")
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}