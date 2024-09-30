package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObject
import com.kotlin.sacalabici.domain.preguntasFrecuentes.ConsultFAQListRequirement
import com.kotlin.sacalabici.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewModel : ViewModel() {
    val faqObjectLiveData = MutableLiveData<FAQObject>()
    private val consultFAQListRequirement = ConsultFAQListRequirement()

    // Usar viewModelScope para ejecutar en un hilo de fondo
    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            // Obtiene la lista de FAQ de forma asíncrona.
            val result: FAQObject? = consultFAQListRequirement(Constants.MAX_FAQ_NUMBER)

            // Verifica si el resultado no es nulo antes de postearlo
            result?.let {
                Log.d("Salida", it.count.toString())
                // Actualiza el LiveData en el hilo principal
                faqObjectLiveData.postValue(it)
            } ?: run {
                // Maneja el caso donde result es null (opcional)
                Log.e("Error", "No se pudo obtener la lista de FAQ")
            }
        }
    }
}
