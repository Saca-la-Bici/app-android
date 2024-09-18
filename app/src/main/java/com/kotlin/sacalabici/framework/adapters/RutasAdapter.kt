package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.ItemRutaBinding

class RutasAdapter: RecyclerView.Adapter<RutasViewHolder>() {

    var data:ArrayList<RutasBase> = ArrayList()

    lateinit var context: Context

    fun RutasAdapter(basicData : ArrayList<RutasBase>, context: Context){
        this.data = basicData
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutasViewHolder {
        val binding = ItemRutaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RutasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RutasViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}