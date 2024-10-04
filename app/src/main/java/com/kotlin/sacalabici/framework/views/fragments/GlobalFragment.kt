package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kotlin.sacalabici.R

class GlobalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_global, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Datos máximos
        val maxAgua = 20
        val maxGasolina = 50
        val maxElectricidad = 200

        // Valores actuales de ahorro
        val ahorroAgua = 12
        val ahorroGasolina = 25
        val ahorroElectricidad = 150

        // Cálculo de porcentajes
        val porcentajeAgua = (ahorroAgua.toFloat() / maxAgua) * 100
        val porcentajeGasolina = (ahorroGasolina.toFloat() / maxGasolina) * 100
        val porcentajeElectricidad = (ahorroElectricidad.toFloat() / maxElectricidad) * 100

        // Asignar las barras de progreso y textos
        val progressAgua = view.findViewById<LinearProgressIndicator>(R.id.progressAgua)
        val textAgua = view.findViewById<TextView>(R.id.textAgua)

        // Configuración de los valores para el progreso y texto
        progressAgua.progress = porcentajeAgua.toInt()
        textAgua.text = "$ahorroAgua L de $maxAgua L"


    }
}
