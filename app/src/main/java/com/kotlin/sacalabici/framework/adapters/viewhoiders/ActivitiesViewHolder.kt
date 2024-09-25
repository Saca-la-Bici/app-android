package com.kotlin.sacalabici.framework.adapters.viewhoiders

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin.sacalabici.data.network.model.ActivityBase
import com.kotlin.sacalabici.databinding.ItemActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitiesViewHolder(
    private val binding: ItemActivityBinding,
    private val longClickListener: (ActivityBase) -> Boolean
): RecyclerView.ViewHolder(binding.root){

    fun bind(item: ActivityBase, context: Context) {
        binding.tvActivityTitle.text = item.title
        binding.tvActivityDate.text = item.date
        binding.tvActivityTime.text = item.time
        binding.tvActivityDuration.text = item.duration
        binding.tvPeopleEnrolled.text = item.peopleEnrolled.toString()

        binding.root.setOnLongClickListener {
            longClickListener(item)
        }

        if(item.imageURL != null){
            binding.ivActivityImage.visibility = View.VISIBLE
            getActivityImage(item.imageURL, binding.ivActivityImage, context)
        } else {
            binding.ivActivityImage.visibility = View.GONE
        }
    }

    private fun getActivityImage(url: String, imageView: ImageView, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.Main).launch {

                val requestOptions = RequestOptions()
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