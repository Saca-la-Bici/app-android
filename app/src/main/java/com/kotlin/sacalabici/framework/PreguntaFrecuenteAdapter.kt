package com.kotlin.sacalabici.framework

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.framework.viewholders.PreguntaFrecuenteViewHolder

// Recibe lista de Pregunta Frecuentes
class PreguntaFrecuenteAdapter(private val preguntafrecuenteList:List<PreguntaFrecuente>) : RecyclerView.Adapter<PreguntaFrecuenteViewHolder>() {
    //Pasarle el item o layout al view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaFrecuenteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PreguntaFrecuenteViewHolder(layoutInflater.inflate(R.layout.item_faq, parent, false))
    }
    // Pasa por cada uno de los items y nos devuelve el render de viewholder
    override fun onBindViewHolder(holder: PreguntaFrecuenteViewHolder, position: Int) {
        val item = preguntafrecuenteList[position]
        holder.render(item)
    }
    override fun getItemCount(): Int = preguntafrecuenteList.size
}