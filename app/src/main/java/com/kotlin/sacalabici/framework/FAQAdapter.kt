package com.kotlin.sacalabici.framework

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.databinding.ItemFaqBinding
import com.kotlin.sacalabici.framework.viewholders.FAQViewHolder

// Clase adaptadora para RecyclerView.
// Responsable de proporcionar los datos del RecyclerView y de crear el ViewHolder para cada uno.
class FAQAdapter: RecyclerView.Adapter<FAQViewHolder>() {

    // Una lista para almacenar los datos que se mostrarán en el RecyclerView.
    var data: ArrayList<com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase> = ArrayList()
    lateinit var context: Context

    // Establece los datos para el adaptador e inicializa el contexto.
    fun FAQAdapter(basicData: ArrayList<com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase>, context: Context) {
        this.data = basicData
        this.context = context
    }


    // Mostrar los datos en el ViewHolder
    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context)
    }

    // Infla el layout del elemento y crea un nuevo PokemonViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Devuelve un nuevo ViewHolder con el binding
        return FAQViewHolder(binding)
    }

    // Devuelve el número total de elementos en la lista de datos.
    override fun getItemCount(): Int = data.size
}
