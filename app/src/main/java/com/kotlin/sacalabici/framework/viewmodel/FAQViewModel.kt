package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.domain.preguntasFrecuentes.FAQListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewModel : ViewModel() {
    val faqObjectLiveData = MutableLiveData<List<FAQBase>>()

    // LiveData para mensajes de error
    val errorMessageLiveData = MutableLiveData<String?>() // Permitir valores nulos
    val emptyListFaq = "Aún no hay datos para mostrar"
    val errorDB = "Error al obtener los datos"

    private val faqListRequirement = FAQListRequirement()
    // private val postFAQRequirement = PostFAQRequirement()

    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = faqListRequirement()
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListFaq)
                } else {
                    errorMessageLiveData.postValue(null) // Limpiar mensaje de error
                }
                faqObjectLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
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
