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
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.announcement.ModifyAnnouncementActivity
import kotlinx.coroutines.launch

class DeleteRouteItemFragment : DialogFragment() {

    private lateinit var viewModel: MapViewModel
    private lateinit var routeId: String
    private lateinit var route: Route

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        arguments?.let {
            routeId = it.getString("id") ?: throw IllegalArgumentException("ID is required")
            route = it.getSerializable("route") as Route ; throw IllegalArgumentException("Route is required")
        } ?: throw IllegalArgumentException("Route is required")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_delete_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDelete: Button = view.findViewById(R.id.btnDel)

        tvDelete.setOnClickListener {
            showDeleteConfirmationRouteDialog()
        }
    }

    private fun showDeleteConfirmationRouteDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.item_delete_route, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_cancelEliminarRuta).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_confirmEliminarRuta).setOnClickListener {
            alertDialog.dismiss()
            deleteRoute()
        }

        alertDialog.show()
    }

    private fun deleteRoute() {
        viewModel.deleteRoute(routeId, route).observe(viewLifecycleOwner) { result ->
            viewLifecycleOwner.lifecycleScope.launch {
                result.fold(
                    onSuccess = {
                        Toast.makeText(requireContext(), "Ruta eliminada exitosamente", Toast.LENGTH_SHORT).show()
                        setFragmentResult("DeleteConfirmationRouteDialogResult", Bundle().apply {
                            putInt("resultCode", RESULT_OK)
                        })
                        dismiss()
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), "Error al eliminar la ruta", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        companion object {
            const val TAG = "DeleteRouteItemFragment"

            fun newInstance(id: String, route: Route): DeleteRouteItemFragment {
                val fragment = DeleteRouteItemFragment()
                val args = Bundle().apply {
                    putString("ID", id) // Cambia "id" por "ID"
                    putSerializable("route", route)
                }
                fragment.arguments = args
                return fragment
            }
        }
    }