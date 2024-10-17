package com.kotlin.sacalabici.framework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.activities.ActivityBase
import com.kotlin.sacalabici.databinding.ItemEventBinding
import com.kotlin.sacalabici.framework.viewholders.ProfileViewHolder
import com.kotlin.sacalabici.framework.viewmodel.ProfileViewModel

class ProfileAdapter (

    private var data: List<ActivityBase>,
    private val clickListener: (ActivityBase) -> Unit,
    private val viewModel: ProfileViewModel  // Se añade el viewModel al constructor
) : RecyclerView.Adapter<ProfileViewHolder>() {

    fun updateData(newData: List<ActivityBase>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(
            binding,
            clickListener,
            viewModel
        ) // Se pasa el viewModel al ViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }
}