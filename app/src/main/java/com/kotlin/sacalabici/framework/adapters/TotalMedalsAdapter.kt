package com.kotlin.sacalabici.framework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.ItemMedalBinding
import com.kotlin.sacalabici.framework.viewholders.TotalMedalsViewHolder

class TotalMedalsAdapter(
    private var data: List<MedalBase>,
) : RecyclerView.Adapter<TotalMedalsViewHolder>() {

    fun updateData(newData: List<MedalBase>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TotalMedalsViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalMedalsViewHolder {
        val binding = ItemMedalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalMedalsViewHolder(binding) // Pasamos el binding al ViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
