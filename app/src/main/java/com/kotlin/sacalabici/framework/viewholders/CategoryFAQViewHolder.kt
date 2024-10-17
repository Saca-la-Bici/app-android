package com.kotlin.sacalabici.framework.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.databinding.ItemFaqCategoryBinding

class CategoryFAQViewHolder(
    private val binding: ItemFaqCategoryBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(category: String) {
        binding.categoryTitle.text = category
    }
}
