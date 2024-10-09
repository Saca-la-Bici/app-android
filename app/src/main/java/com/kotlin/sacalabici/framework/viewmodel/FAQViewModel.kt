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

    val errorMessage = MutableLiveData<String?>()
    val selectedFAQ = MutableLiveData<FAQBase?>()

    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: FAQObjectBase? = faqListRequirement()
                val faqresult = result!!.faqs

                if (faqresult.isEmpty()) {
                    errorMessage.postValue("No se encontraron preguntas frecuentes")
                } else {
                    faqObjectLiveData.postValue(faqresult)
                    errorMessage.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.postValue("Error al consultar los datos")
                faqObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun selectFAQ(faq: FAQBase) {
        selectedFAQ.postValue(faq)
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
