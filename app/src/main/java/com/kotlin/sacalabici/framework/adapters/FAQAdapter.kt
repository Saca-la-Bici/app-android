package com.kotlin.sacalabici.framework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.ItemFaqBinding
import com.kotlin.sacalabici.framework.viewholders.FAQViewHolder

class FAQAdapter(
    private var data: List<FAQBase>,
    private val longClickListener: (FAQBase) -> Boolean,
) : RecyclerView.Adapter<FAQViewHolder>() {
    fun updateData(newData: List<FAQBase>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: FAQViewHolder,
        position: Int,
    ) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding, longClickListener)
    }

    override fun getItemCount(): Int = data.size
}
