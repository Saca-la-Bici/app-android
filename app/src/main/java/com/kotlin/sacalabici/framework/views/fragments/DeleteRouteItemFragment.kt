package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.announcement.ModifyAnnouncementActivity
import kotlinx.coroutines.launch

class DeleteRouteItemFragment : DialogFragment() {

    private lateinit var viewModel: MapViewModel
    private lateinit var rutaid: String

    private lateinit var btnCancel: Button
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        arguments?.let {
            rutaid = it.getString("ID") ?: throw IllegalArgumentException("ID is required")
        } ?: throw IllegalArgumentException("Route is required")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Accedemos a los botones del layout
        btnCancel = view.findViewById(R.id.btn_cancelEliminarRuta)
        btnConfirm = view.findViewById(R.id.btn_confirmEliminarRuta)

        // Acción para confirmar eliminación
        btnConfirm.setOnClickListener {
            deleteRoute() // Llamar directamente a la función de eliminación
        }

        // Acción para cancelar
        btnCancel.setOnClickListener {
            dismiss() // Simplemente cerrar el diálogo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout
        return inflater.inflate(R.layout.item_delete_route, container, false)
    }

    private fun deleteRoute() {
        viewModel.deleteRoute(rutaid) { result ->
            viewLifecycleOwner.lifecycleScope.launch {
                result.fold(
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Ruta eliminada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        setFragmentResult("DeleteConfirmationRouteDialogResult", Bundle().apply {
                            putInt("resultCode", RESULT_OK)
                        })
                        dismiss() // Cerramos el diálogo después de eliminar la ruta
                    },
                    onFailure = {
                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar la ruta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }

    companion object {
        const val TAG = "DeleteRouteItemFragment"
        fun newInstance(id: String): DeleteRouteItemFragment {
            val fragment = DeleteRouteItemFragment()
            val args = Bundle().apply {
                putString("ID", id)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
