package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import com.kotlin.sacalabici.domain.preguntasFrecuentes.FAQListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewModel : ViewModel() {
    val faqObjectLiveData = MutableLiveData<List<FAQBase>>()
    private val faqListRequirement = FAQListRequirement()
    // private val postFAQRequirement = PostFAQRequirement()

    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: FAQObjectBase? = faqListRequirement()
                val faqresult = result!!.faqs.reversed()
                faqObjectLiveData.postValue(faqresult)
            } catch (e: Exception) {
                e.printStackTrace()
                faqObjectLiveData.postValue(emptyList())
            }
        }
    }

    /*
    fun postFAQ(FAQ: FAQ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postFAQRequirement(FAQ)
            } catch (e: Exception) {
                throw e
            }
        }
    }

     */
}
