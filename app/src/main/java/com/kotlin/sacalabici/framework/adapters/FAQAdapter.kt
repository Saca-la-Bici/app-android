package com.kotlin.sacalabici.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.ItemFaqBinding
import com.kotlin.sacalabici.databinding.ItemFaqCategoryBinding
import com.kotlin.sacalabici.framework.viewholders.CategoryFAQViewHolder
import com.kotlin.sacalabici.framework.viewholders.FAQViewHolder
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

sealed class FAQItem {
    data class Category(
        val name: String,
    ) : FAQItem()

    data class FAQ(
        val faqBase: FAQBase,
    ) : FAQItem()
}

class FAQAdapter(
    private val viewModel: FAQViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: ArrayList<FAQItem> = ArrayList()
    lateinit var context: Context

    companion object {
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_FAQ = 1
    }

    fun setFAQAdapter(
        basicData: ArrayList<FAQBase>,
        context: Context,
    ) {
        this.context = context
        val groupedData = basicData.groupBy { it.Tema }
        data.clear()
        for ((category, faqs) in groupedData) {
            data.add(FAQItem.Category(category))
            faqs.forEach { faq -> data.add(FAQItem.FAQ(faq)) }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = data[position]
        if (getItemViewType(position) == VIEW_TYPE_CATEGORY) {
            (holder as CategoryFAQViewHolder).bind((item as FAQItem.Category).name)
        } else {
            (holder as FAQViewHolder).bind((item as FAQItem.FAQ).faqBase)
            holder.itemView.setOnClickListener {
                viewModel.selectFAQ(item.faqBase)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_CATEGORY) {
            val binding = ItemFaqCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CategoryFAQViewHolder(binding)
        } else {
            val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FAQViewHolder(binding)
        }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int =
        if (data[position] is FAQItem.Category) {
            VIEW_TYPE_CATEGORY
        } else {
            VIEW_TYPE_FAQ
        }

    fun updateList(filteredData: ArrayList<FAQBase>) {
        data.clear() // Clear current data
        // Group filtered data by category and rebuild the list
        val groupedData = filteredData.groupBy { it.Tema }
        for ((category, faqs) in groupedData) {
            data.add(FAQItem.Category(category)) // Add category
            faqs.forEach { faq -> data.add(FAQItem.FAQ(faq)) } // Add filtered FAQs
        }
        notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
    }
}