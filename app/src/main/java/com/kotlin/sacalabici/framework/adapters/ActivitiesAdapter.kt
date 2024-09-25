package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.network.model.ActivityBase
import com.kotlin.sacalabici.databinding.ItemActivityBinding
import com.kotlin.sacalabici.framework.adapters.viewhoiders.ActivitiesViewHolder

class ActivitiesAdapter(
    private val data: ArrayList<ActivityBase>,
    private val context: Context,
    private val longClickListener: (ActivityBase) -> Boolean
): RecyclerView.Adapter<ActivitiesViewHolder>(){

    fun updateData(newData: List<ActivityBase>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item,context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ActivitiesViewHolder(binding, longClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
