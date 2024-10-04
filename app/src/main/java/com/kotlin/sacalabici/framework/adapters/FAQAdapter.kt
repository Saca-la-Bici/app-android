package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.ItemFaqBinding
import com.kotlin.sacalabici.framework.viewholders.FAQViewHolder

class FAQAdapter : RecyclerView.Adapter<FAQViewHolder>() {
    var data: ArrayList<FAQBase> = ArrayList()
    lateinit var context: Context

    fun setFAQAdapter(
        basicData: ArrayList<FAQBase>,
        context: Context,
    ) {
        this.data = basicData
        this.context = context
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
        return FAQViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size
}
