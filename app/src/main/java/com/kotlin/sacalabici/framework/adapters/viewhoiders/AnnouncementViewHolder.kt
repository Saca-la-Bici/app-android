package com.kotlin.sacalabici.framework.adapters.viewhoiders

import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.ItemAnnouncementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnnouncementViewHolder(private val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: AnnouncementBase, context: Context){
        binding.tvAnnouncementTitle.text = item.title
        binding.tvAnnouncementContent.text = item.content
        getAnnouncementImg(item.url,binding.ivAnnouncement,context)

    }

    private fun getAnnouncementImg(url:String, imageView: ImageView, context: Context){

        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.Main).launch {

                val requestOptions =  RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .priority(Priority.HIGH)

                Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView)
            }
        }}
}