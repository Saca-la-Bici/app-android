package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentSettingsAdminBinding
import com.kotlin.sacalabici.framework.views.activities.session.SessionActivity
import com.kotlin.sacalabici.framework.views.fragments.ProfileFragment


class SettingsAdminFragment : Fragment() {
    private var _binding: FragmentSettingsAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsAdminBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()

        if (storedPermissions?.contains("el permiso que necesitas") != true) {
            binding.btnRoles.visibility = View.GONE
        }

        binding.btnRoles.setOnClickListener {
            val rolAdministradorFragment = RolAdministradorFragment()
            // Reemplazar el fragmento actual por SettingsFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, rolAdministradorFragment) // Asegúrate de que este ID coincida con el contenedor de fragmentos en tu layout
                .addToBackStack(null) // Para permitir volver al fragmento anterior
                .commit()
        }

        binding.BBack.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, profileFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.BLogOut.setOnClickListener {
            // Sign out the user from Firebase
            Firebase.auth.signOut()

            // Check if the user is signed out successfully
            if (Firebase.auth.currentUser == null) {
                // User is signed out, navigate to activity_session
                val intent = Intent(activity, SessionActivity::class.java) // Replace with the correct class name for your activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear the back stack
                startActivity(intent)
                activity?.finish() // Finish the current activity if necessary
            } else {
                // Handle the case where sign-out failed (optional)
                Toast.makeText(context, "Sign out failed", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Liberar el binding
    }
}
