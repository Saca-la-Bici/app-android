package com.kotlin.sacalabici.framework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ItemActivityBinding
import com.kotlin.sacalabici.framework.viewholders.ActivitiesViewHolder
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel

class ActivitiesAdapter(
    private var data: List<Activity>,
    private val clickListener: (Activity) -> Unit,
    private val viewModel: ActivitiesViewModel
) : RecyclerView.Adapter<ActivitiesViewHolder>() {

    // Actualizar lista de actividades y notificar al adaptador del cambio
    fun updateData(newData: List<Activity>) {
        data = newData
        notifyDataSetChanged()
    }

    // Vincular datos del elemento actividad en la posición indicada al ViewHolder
    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    // Inflar diseño del elemento actividad y crear nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivitiesViewHolder(binding, clickListener, viewModel)
    }

    // Indicar al RecyclerViewcuantos elementos mostrar
    override fun getItemCount(): Int {
        return data.size
    }
}
