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

// Definir FAQItem para manejar categorías y FAQs
sealed class FAQItem {
    data class Category(
        val name: String,
    ) : FAQItem()

    data class FAQ(
        val faqBase: FAQBase,
    ) : FAQItem()
}

class FAQAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: ArrayList<FAQItem> = ArrayList() // Holds FAQ items including categories
    var dataSearch: ArrayList<FAQBase> = ArrayList() // Used for filtering/searching
    lateinit var context: Context

    companion object {
        // Tipos de vista para el RecyclerView
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_FAQ = 1
    }

    // Function to set the initial data and organize it by category
    fun setFAQAdapter(
        basicData: ArrayList<FAQBase>,
        context: Context,
    ) {
        this.context = context
        // Group by the category (Tema field in FAQBase)
        val groupedData = basicData.groupBy { it.Tema }
        data.clear()
        for ((category, faqs) in groupedData) {
            data.add(FAQItem.Category(category)) // Add the category header
            faqs.forEach { faq -> data.add(FAQItem.FAQ(faq)) } // Add FAQs under that category
        }
        // Notify the adapter that the data has changed
        notifyDataSetChanged()
    }

    // Binding the view holder based on the item type (category or FAQ)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = data[position]
        if (getItemViewType(position) == VIEW_TYPE_CATEGORY) {
            (holder as CategoryFAQViewHolder).bind((item as FAQItem.Category).name)
        } else {
            (holder as FAQViewHolder).bind((item as FAQItem.FAQ).faqBase)
        }
    }

    // Creating the view holder for the item
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

    // Returns the number of items
    override fun getItemCount(): Int = data.size

    // Determine the view type (Category or FAQ)
    override fun getItemViewType(position: Int): Int =
        if (data[position] is FAQItem.Category) {
            VIEW_TYPE_CATEGORY
        } else {
            VIEW_TYPE_FAQ
        }

    // Function to update the list dynamically when search/filter is applied
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
