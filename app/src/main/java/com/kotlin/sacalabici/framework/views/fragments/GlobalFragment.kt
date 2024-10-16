package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentGlobalBinding

class GlobalFragment : Fragment() {

    private var _binding: FragmentGlobalBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar el binding
        _binding = FragmentGlobalBinding.inflate(inflater, container, false)

        // Inicializar el ViewModel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Retornar la vista raíz desde el binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observar el perfil y actualizar los valores cuando cambien
        viewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                // Obtener los kilómetros recorridos del perfil
                val kilometers = profile.KmCompleted
                val kilometersMonth = profile.kilometrosMes.toFloat()

                // Datos máximos
                val maxWater = 80
                val maxCO2 = 30
                val maxAir = 5
                val maxGas = 30
                val maxKilometrosMes = 250

                // Valores de referencia
                val versaGasConsume = 9 // km por litro
                val emissionCO2 = 2.31 // kg de CO2 por litro de gasolina
                val NOx = 0.05

                // Valores actuales de ahorro
                val water = (kilometers / versaGasConsume) * 3
                val co2 = (kilometers / versaGasConsume) * emissionCO2
                val air = kilometers * NOx
                val gas = kilometers / versaGasConsume

                // Cálculo de porcentajes
                val percentageWater = (water.toFloat() / maxWater) * 100
                val percentageCO2 = (co2.toFloat() / maxCO2) * 100
                val percentageAir = (air.toFloat() / maxAir) * 100
                val percentageGas = (gas.toFloat() / maxGas) * 100
                val percentageKilometrosMes = (kilometersMonth / maxKilometrosMes) * 100

                // Configuración de los valores para el progreso y texto
                binding.progressKilometrosMes.progress = percentageKilometrosMes.toInt()
                binding.textResultKilometrosMes.text = String.format("%.2f km", percentageKilometrosMes)

                binding.progressAgua.progress = percentageWater.toInt()
                binding.textResultWater.text = String.format("%.2f L", water)


                binding.progressCO2.progress = percentageCO2.toInt()
                binding.textResultCO2.text = String.format("%.2f kg", co2)

                binding.progressAir.progress = percentageAir.toInt()
                binding.textResultAir.text = String.format("%.2f g", air)

                binding.progressGas.progress = percentageGas.toInt()
                binding.textResultGas.text = String.format("%.2f L", gas)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
