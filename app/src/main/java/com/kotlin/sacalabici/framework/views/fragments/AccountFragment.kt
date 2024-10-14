package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.databinding.FragmentAccountBinding
import com.kotlin.sacalabici.framework.adapters.views.fragments.SettingsFragment
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import kotlinx.coroutines.launch

class AccountFragment: Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private lateinit var viewModel: ProfileViewModel
    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val root: View = binding.root
        setUpBackButton()
        setUpEliminateButton()

        return root
    }


    private fun setUpBackButton() {
        val btnFAQs = binding.BBack
        btnFAQs.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setUpEliminateButton() {
        val btnEliminate = binding.BEraseAccount
        btnEliminate.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun eliminateAccount(){
        lifecycleScope.launch {
            viewModel.deleteProfile() { result ->
                viewLifecycleOwner.lifecycleScope.launch {
                    result.fold(
                        onSuccess = {
                            Toast.makeText(requireContext(), "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show()
                            setFragmentResult("actionButtonDialogResult", Bundle().apply {
                                putInt("resultCode", RESULT_OK)
                            })
                            Firebase.auth.signOut()

                            // Check if the user is signed out successfully
                            if (Firebase.auth.currentUser == null) {
                                // User is signed out, navigate to activity_session
                                val intent = Intent(
                                    activity,
                                    SessionActivity::class.java
                                ) // Replace with the correct class name for your activity
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear the back stack
                                startActivity(intent)
                                activity?.finish() // Finish the current activity if necessary
                            } else {
                                // Handle the case where sign-out failed (optional)
                                Toast.makeText(context, "Error al eliminar cuenta", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        onFailure = { error ->
                            Toast.makeText(requireContext(), "Error al eliminar el anuncio: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.item_delete_account, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            alertDialog.dismiss()
            eliminateAccount()
        }
        alertDialog.show()
    }


}
