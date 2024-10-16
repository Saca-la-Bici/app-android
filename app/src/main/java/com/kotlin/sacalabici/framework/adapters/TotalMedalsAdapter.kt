package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.databinding.ItemMedalBinding
import com.kotlin.sacalabici.framework.viewholders.TotalMedalsViewHolder

class TotalMedalsAdapter : RecyclerView.Adapter<TotalMedalsViewHolder>() {
    var data: ArrayList<MedalBase> = ArrayList()
    lateinit var context: Context

    fun setTotalMedalsAdapter(basicData: ArrayList<MedalBase>, context: Context) {
        this.data = basicData
        this.context = context
    }

    override fun onBindViewHolder(holder: TotalMedalsViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalMedalsViewHolder {
        val binding = ItemMedalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalMedalsViewHolder(binding) // Pasamos el binding al ViewHolder
    }

    override fun getItemCount(): Int = data.size
}
