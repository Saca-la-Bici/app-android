package com.kotlin.sacalabici.framework.views.fragments

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
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.activities.ModifyActivityActivity

class ActivityActionDialogFragment: DialogFragment() {

    private lateinit var viewModel: ActivitiesViewModel
    private lateinit var activity: Activity
    private var permissions: List<String> = emptyList()

    private lateinit var id: String
    private lateinit var title: String
    private lateinit var date: String
    private lateinit var hour: String
    private lateinit var ubi: String
    private lateinit var desc: String
    private lateinit var hourDur: String
    private var url: String? = null
    private lateinit var type: String
    private var level: String? = null
    private var distance: String? = null

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
            url = it.getString("url")
            type = it.getString("type") ?: throw IllegalArgumentException("Type is required")
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

        tvDelete.visibility = View.GONE
        tvModify.text = "Modificar actividad"

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
                putExtra("type", type)
            }
            startActivity(intent)
            dismiss()
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
            type: String,
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
            args.putString("type", type)
            args.putStringArrayList("permissions", ArrayList(permissions))
            fragment.arguments = args
            return fragment
        }
    }
}