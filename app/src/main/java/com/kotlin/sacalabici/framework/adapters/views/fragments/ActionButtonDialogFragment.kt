import android.app.AlertDialog
import android.app.Dialog
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.FirebaseTokenManager
import com.kotlin.sacalabici.framework.adapters.viewmodel.AnnouncementsViewModel
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase

class ActionButtonDialogFragment : DialogFragment() {

    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var announcement: AnnouncementBase
    private lateinit var firebaseTokenManager: FirebaseTokenManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AnnouncementsViewModel::class.java)
        arguments?.let {
            val id = it.getString("id") ?: throw IllegalArgumentException("ID is required")
            val title = it.getString("title") ?: throw IllegalArgumentException("Title is required")
            val content = it.getString("content") ?: throw IllegalArgumentException("Content is required")
            val url = it.getString("url") // url can be null
            announcement = AnnouncementBase(id, title, content, url ?: "")
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

        tvDelete.setOnClickListener {
            // Inflate the custom layout
            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.item_delete_message, null)

            // Create the AlertDialog
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)

            val alertDialog = builder.create()

            // Set the background of the AlertDialog to be transparent
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set click listeners for the buttons in the custom layout
            dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                alertDialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                viewModel.deleteAnnouncement(announcement.id)
                Log.d("click", "Delete confirmed")
                alertDialog.dismiss()
                dismiss()
            }

            alertDialog.show()
        }

        tvModify.setOnClickListener {
            // Handle modify action
            Log.d("click", "Modify")
        }
    }

    companion object {
        const val TAG = "ActionButtonDialogFragment"
        fun newInstance(id: String, title: String, content: String, url: String?): ActionButtonDialogFragment {
            val fragment = ActionButtonDialogFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("title", title)
            args.putString("content", content)
            args.putString("url", url)
            fragment.arguments = args
            return fragment
        }
    }
}