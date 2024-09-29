package com.kotlin.sacalabici.framework.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente

class PreguntaFrecuenteViewHolder(val view: View): RecyclerView.ViewHolder(view){

    val preguntaFrecuente = view.findViewById<TextView>(R.id.preguntafrecuentedisplay)

    fun render(preguntaFrecuenteModel: PreguntaFrecuente){
        preguntaFrecuente.text = preguntaFrecuenteModel.Pregunta //La variable Pregunta viene del modelo
    }
}
