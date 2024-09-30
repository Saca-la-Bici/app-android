package com.kotlin.sacalabici.framework.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.sacalabici.databinding.FragmentRegisterFaqBinding

class RegisterFAQFragment : Fragment() {
    private var _binding: FragmentRegisterFaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflar el layout y obtener el binding
        _binding = FragmentRegisterFaqBinding.inflate(inflater, container, false)

        // Retorna la vista raíz del binding
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
