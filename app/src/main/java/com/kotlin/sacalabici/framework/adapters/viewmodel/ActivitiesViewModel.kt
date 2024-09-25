package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ActivityBase
import com.kotlin.sacalabici.domain.GetEventosRequirement
import com.kotlin.sacalabici.domain.GetRodadasRequirement
import com.kotlin.sacalabici.domain.GetTalleresRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitiesViewModel: ViewModel() {
    // LiveData para observar los datos de la UI
    val rodadasLiveData = MutableLiveData<List<ActivityBase>>()
    val eventosLiveData = MutableLiveData<List<ActivityBase>>()
    val talleresLiveData = MutableLiveData<List<ActivityBase>>()

    // Requisitos para obtener los datos
    private val getRodadasRequirement = GetRodadasRequirement()
    private val getEventosRequirement = GetEventosRequirement()
    private val getTalleresRequirement = GetTalleresRequirement()

    // Función para cargar rodadas
    fun getRodadas() {
        viewModelScope.launch(Dispatchers.IO) {
            val result: List<ActivityBase> = getRodadasRequirement()
            launch(Dispatchers.Main) {
                rodadasLiveData.postValue(result)
            }
        }
    }

    // Función para cargar eventos
    fun getEventos() {
        viewModelScope.launch(Dispatchers.IO) {
            val result: List<ActivityBase> = getEventosRequirement()
            launch(Dispatchers.Main) {
                eventosLiveData.postValue(result)
            }
        }
    }

    // Función para cargar talleres
    fun getTalleres() {
        viewModelScope.launch(Dispatchers.IO) {
            val result: List<ActivityBase> = getTalleresRequirement()
            launch(Dispatchers.Main) {
                talleresLiveData.postValue(result)
            }
        }
    }
}