package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository
import com.kotlin.sacalabici.domain.preguntasFrecuentes.ReviewFaqRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FaqViewModel():ViewModel() {

    private val _preguntaObjectLiveData = MutableLiveData<PreguntaFrecuente>()
    val preguntaObjectLiveData: MutableLiveData<PreguntaFrecuente> = _preguntaObjectLiveData
    private val reviewFaqRequirement = ReviewFaqRequirement()

    fun getPreguntaIndividual(IdPregunta:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                Log.d("flow", "Entra ViewModel")
                val result : PreguntaFrecuente = reviewFaqRequirement(IdPregunta)
                preguntaObjectLiveData.postValue(result)
            } catch (err:Exception){
                err.printStackTrace()
            }
        }
    }
}