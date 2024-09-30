package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject
import com.kotlin.sacalabici.domain.preguntasFrecuentes.ConsultFAQListRequirement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FAQViewModel : ViewModel() {
    val FAQObjectLiveData = MutableLiveData<FAQObject>()
    private val consultFAQListRequirement = ConsultFAQListRequirement()

    // Usar viewModelScope para ejecutar en un hilo de fondo
    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: FAQObject? = consultFAQListRequirement()
                withContext(Dispatchers.Main) {
                    result?.let {
                        FAQObjectLiveData.postValue(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

