// AnnouncementAdapter.kt
package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.ItemAnnouncementBinding
import com.kotlin.sacalabici.framework.adapters.viewhoiders.AnnouncementViewHolder

class AnnouncementAdapter(
    private val longClickListener: (AnnouncementBase) -> Boolean
) : RecyclerView.Adapter<AnnouncementViewHolder>() {
    var data: ArrayList<AnnouncementBase> = ArrayList()
    lateinit var context: Context

    fun AnnouncementAdapter(basicData: ArrayList<AnnouncementBase>, context: Context) {
        this.data = basicData
        this.context = context
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding, longClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}