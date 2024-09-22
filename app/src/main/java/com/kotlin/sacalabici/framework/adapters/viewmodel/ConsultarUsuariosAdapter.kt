package com.kotlin.sacalabici.framework.adapters.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.ItemUserBinding
import com.kotlin.sacalabici.framework.adapters.viewhoiders.ConsultarUsuariosViewHolder

class ConsultarUsuariosAdapter: RecyclerView.Adapter<ConsultarUsuariosViewHolder>() {
    var data:ArrayList<ConsultarUsuariosBase> = ArrayList()

    fun updateData(basicData: ArrayList<ConsultarUsuariosBase>){
        this.data = basicData
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultarUsuariosViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsultarUsuariosViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ConsultarUsuariosViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}