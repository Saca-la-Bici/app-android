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
    private val longClickListener: (Activity) -> Unit,
    private val viewModel: ActivitiesViewModel  // Se añade el viewModel al constructor
) : RecyclerView.Adapter<ActivitiesViewHolder>() {

    fun updateData(newData: List<Activity>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivitiesViewHolder(binding, clickListener, longClickListener, viewModel) // Se pasa el viewModel al ViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
