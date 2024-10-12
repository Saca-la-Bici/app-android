import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
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
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel
import com.kotlin.sacalabici.framework.views.activities.announcement.ModifyAnnouncementActivity
import kotlinx.coroutines.launch

class ActionButtonDialogFragment : DialogFragment() {

    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var announcement: AnnouncementBase
    private var permissions: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AnnouncementsViewModel::class.java)
        arguments?.let {
            val id = it.getString("id") ?: throw IllegalArgumentException("ID is required")
            val title = it.getString("title") ?: throw IllegalArgumentException("Title is required")
            val content = it.getString("content") ?: throw IllegalArgumentException("Content is required")
            val url = it.getString("url") // url can be null
            permissions = it.getStringArrayList("permissions") ?: emptyList()
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
        Log.d("ActionButtonDialogFragment", "onViewCreated: $permissions")

        if (!permissions.contains("Eliminar anuncio")) {
            tvDelete.visibility = View.GONE
        }

        if (!permissions.contains("Modificar anuncio")) {
            tvModify.visibility = View.GONE
        }

        tvDelete.setOnClickListener {
            showDeleteConfirmationDialog()
            dismiss()
        }
        tvModify.setOnClickListener {
            val intent = Intent(requireContext(), ModifyAnnouncementActivity::class.java).apply {
                putExtra("id", announcement.id)
                putExtra("title", announcement.title)
                putExtra("content", announcement.content)
                putExtra("url", announcement.url)
            }
            startActivity(intent)
            dismiss()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.item_delete_message, null)

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

            // Ejecutar la acción de eliminar y esperar su finalización
            deleteAnnouncement { success ->
                // Si la eliminación fue exitosa, cerrar el diálogo
                if (success) {
                    alertDialog.dismiss()
                } else {
                    // Si hubo un error, volver a habilitar el botón
                    button.isEnabled = true
                    Toast.makeText(requireContext(), "Error al eliminar el anuncio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    private fun deleteAnnouncement(onComplete: (Boolean) -> Unit) {
        viewModel.viewModelScope.launch {
            viewModel.deleteAnnouncement(announcement.id) { result ->
                result.fold(
                    onSuccess = {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Anuncio eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            setFragmentResult("actionButtonDialogResult", Bundle().apply {
                                putInt("resultCode", RESULT_OK)
                            })
                            onComplete(true) // Llamar onComplete con éxito
                        }
                    },
                    onFailure = { error ->
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Error al eliminar el anuncio: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                        onComplete(false) // Llamar onComplete con fallo
                    }
                )
            }
        }
    }


    companion object {
        const val TAG = "ActionButtonDialogFragment"
        fun newInstance(id: String, title: String, content: String, url: String?, permissions: List<String>): ActionButtonDialogFragment {
            val fragment = ActionButtonDialogFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("title", title)
            args.putString("content", content)
            args.putString("url", url)
            args.putStringArrayList("permissions", ArrayList(permissions))
            fragment.arguments = args
            return fragment
        }
    }
}