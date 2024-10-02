package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository
import kotlinx.coroutines.launch

class FaqViewModel(private val repository: PreguntasFrecuentesRepository):ViewModel() {
    val pregunta: LiveData<PreguntaFrecuente> = MutableLiveData()
    val IdPregunta : Int = 3

    val preguntaFrecuente: LiveData<PreguntaFrecuente> = liveData {
        val data = repository.consultarPreguntaFrecuenteInd(IdPregunta)
        if (data != null) {
            emit(data)
        }
    }

    fun getPreguntaIndividual(idPregunta: Int) {
        viewModelScope.launch {
            val data = repository.consultarPreguntaFrecuenteInd(idPregunta)
            (pregunta as MutableLiveData).value = data
        }
    }
}