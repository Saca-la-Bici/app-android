package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import com.kotlin.sacalabici.domain.preguntasFrecuentes.FAQListRequirement
import com.kotlin.sacalabici.domain.preguntasFrecuentes.PostFAQRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewModel : ViewModel() {
    val faqObjectLiveData = MutableLiveData<List<FAQBase>>()
    val permissionsLiveData = MutableLiveData<List<String>>()
    private val faqListRequirement = FAQListRequirement()
    private val postFAQRequirement = PostFAQRequirement()

    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: FAQObjectBase? = faqListRequirement()
                val faqresult = result!!.faqs.reversed()
                faqObjectLiveData.postValue(faqresult)
                permissionsLiveData.postValue(result.permissions)
            } catch (e: Exception) {
                e.printStackTrace()
                faqObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun postFAQ(faq: FAQBase) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postFAQRequirement(faq)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
