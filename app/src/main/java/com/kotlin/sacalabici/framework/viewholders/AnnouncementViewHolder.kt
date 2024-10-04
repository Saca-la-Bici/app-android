package com.kotlin.sacalabici.framework.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.ItemAnnouncementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnnouncementViewHolder(
    private val binding: ItemAnnouncementBinding,
    private val longClickListener: (AnnouncementBase) -> Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AnnouncementBase, context: Context) {
        binding.tvAnnouncementTitle.text = item.title
        binding.tvAnnouncementContent.text = item.content

        binding.root.setOnLongClickListener {
            longClickListener(item)
        }
        if(item.url != null){
            binding.ivAnnouncement.visibility = View.VISIBLE
            getAnnouncementImg(item.url,binding.ivAnnouncement,context)
        } else {
            binding.ivAnnouncement.visibility = View.GONE
        }
    }

    private fun getAnnouncementImg(url: String, imageView: ImageView, context: Context) {
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
        }
    }
}