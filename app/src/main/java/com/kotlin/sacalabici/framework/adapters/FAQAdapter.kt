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
    var data: ArrayList<FAQItem> = ArrayList()
    lateinit var context: Context

    companion object {
        // Tipos de vista para el RecyclerView
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_FAQ = 1
    }

    fun setFAQAdapter(
        basicData: ArrayList<FAQBase>,
        context: Context,
    ) {
        this.context = context
        // Agrupamos por categoría utilizando el campo Tema
        val groupedData = basicData.groupBy { it.Tema }
        data.clear()
        for ((category, faqs) in groupedData) {
            data.add(FAQItem.Category(category)) // Añadimos el encabezado de la categoría
            faqs.forEach { faq -> data.add(FAQItem.FAQ(faq)) } // Añadimos las FAQ dentro de esa categoría
        }
        // Notificamos que los datos han cambiado para actualizar la vista
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

    // Verificamos el tipo de vista para determinar cómo enlazar los datos
    override fun getItemViewType(position: Int): Int =
        if (data[position] is FAQItem.Category) {
            VIEW_TYPE_CATEGORY
        } else {
            VIEW_TYPE_FAQ
        }
}
