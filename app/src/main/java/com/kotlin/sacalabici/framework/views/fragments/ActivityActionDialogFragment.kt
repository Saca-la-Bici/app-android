package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.ModifyActivityActivity
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class ActivityActionDialogFragment: DialogFragment() {

    private lateinit var viewModel: ActivitiesViewModel
    private var permissions: List<String> = emptyList()

    private lateinit var id: String
    private lateinit var title: String
    private lateinit var date: String
    private lateinit var hour: String
    private lateinit var ubi: String
    private lateinit var desc: String
    private lateinit var hourDur: String
    private var url: String? = null
    private var peopleEnrolled: Int by Delegates.notNull()
    private var state: Boolean by Delegates.notNull()
    private var foro: String? = null
    private var register: ArrayList<String>? = null
    private lateinit var typeAct: String
    private var idRoute: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)
        arguments?.let {
            id = it.getString("id") ?: throw IllegalArgumentException("ID is required")
            title = it.getString("title") ?: throw IllegalArgumentException("Title is required")
            date = it.getString("date") ?: throw IllegalArgumentException("Date is required")
            hour = it.getString("hour") ?: throw IllegalArgumentException("Hour is required")
            ubi = it.getString("ubi") ?: throw IllegalArgumentException("Ubi is required")
            desc = it.getString("desc") ?: throw IllegalArgumentException("Description is required")
            hourDur = it.getString("hourDur") ?: throw IllegalArgumentException("Hour duration is required")
            typeAct = it.getString("typeAct") ?: throw IllegalArgumentException("Type duration is required")
            url = it.getString("url")
            peopleEnrolled = it.getInt("peopleEnrolled") ?: throw IllegalArgumentException("People enrolled is required")
            state = it.getBoolean("state") ?: throw IllegalArgumentException("State is required")
            foro = it.getString("foro")
            register = it.getStringArrayList("register")
            idRoute = it.getString("idRoute")
            permissions = it.getStringArrayList("permissions") ?: emptyList()
        } ?: throw IllegalArgumentException("Arguments are required")
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
        return inflater.inflate(R.layout.item_action_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDelete: TextView = view.findViewById(R.id.TVDelete)
        val tvModify: TextView = view.findViewById(R.id.TVModify)

        tvDelete.text = "Eliminar actividad"
        tvModify.text = "Modificar actividad"

        tvDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        tvModify.setOnClickListener {
            val intent = Intent(requireContext(), ModifyActivityActivity::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("date", date)
                putExtra("hour", hour)
                putExtra("ubi", ubi)
                putExtra("desc", desc)
                putExtra("hourDur", hourDur)
                putExtra("url", url)
                putExtra("typeAct", typeAct)
                putExtra("peopleEnrolled", peopleEnrolled)
                putExtra("state", state)
                putExtra("foro", foro)
                putExtra("register", register)
                putExtra("idRoute", idRoute)
            }
            startActivity(intent)
            dismiss()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.item_delete_activity, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener { button ->
            // Desactivar el botón para evitar múltiples clics
            button.isEnabled = false

            deleteActivity { success ->
                // Si la eliminación fue exitosa, cerrar el diálogo
                if (success) {
                    alertDialog.dismiss()
                    this@ActivityActionDialogFragment.dismiss()
                } else {
                    // Si hubo un error, volver a habilitar el botón
                    button.isEnabled = true
                    Toast.makeText(requireContext(), "Error al eliminar el anuncio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    private fun deleteActivity(onComplete: (Boolean) -> Unit) {
        viewModel.viewModelScope.launch {
            viewModel.deleteActivity(id, typeAct) { result ->
                result.fold(
                    onSuccess = {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Actividad eliminada exitosamente", Toast.LENGTH_SHORT).show()
                            val sharedPreferences = requireContext().getSharedPreferences("activity_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("activity_updated", true)
                            editor.apply()
                            setFragmentResult("activityActionDialogResult", Bundle().apply {
                                putInt("resultCode", RESULT_OK)
                            })
                            onComplete(true) // Llamar onComplete con éxito
                        }
                    },
                    onFailure = { error ->
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Error al eliminar la actividad: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                        onComplete(false) // Llamar onComplete con fallo
                    }
                )
            }
        }
    }

    companion object {
        const val TAG = "ActivityActionDialogFragment"
        fun newInstance(
            id: String,
            title: String,
            date: String,
            hour: String,
            ubi: String,
            desc: String,
            hourDur: String,
            url: String?,
            typeAct: String,
            peopleEnrolled: Int,
            state: Boolean,
            foro: String?,
            register: ArrayList<String>?,
            idRoute: String?,
            permissions: List<String>
        ): ActivityActionDialogFragment {
            val fragment = ActivityActionDialogFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("title", title)
            args.putString("date", date)
            args.putString("hour", hour)
            args.putString("ubi", ubi)
            args.putString("desc", desc)
            args.putString("hourDur", hourDur)
            args.putString("url", url)
            args.putString("typeAct", typeAct)
            args.putInt("peopleEnrolled", peopleEnrolled)
            args.putBoolean("state", state)
            args.putString("foro", foro)
            args.putStringArrayList("register", register)
            args.putString("idRoute", idRoute)
            args.putStringArrayList("permissions", ArrayList(permissions))
            fragment.arguments = args
            return fragment
        }
    }
}