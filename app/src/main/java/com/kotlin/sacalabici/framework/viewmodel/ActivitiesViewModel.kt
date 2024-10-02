package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.domain.activities.GetEventosRequirement
import com.kotlin.sacalabici.domain.activities.GetRodadasRequirement
import com.kotlin.sacalabici.domain.activities.GetTalleresRequirement
import com.kotlin.sacalabici.domain.activities.PostJoinActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitiesViewModel: ViewModel() {
    // LiveData para observar los datos de la UI
    val rodadasLiveData = MutableLiveData<List<Activity>>()
    val eventosLiveData = MutableLiveData<List<Activity>>()
    val talleresLiveData = MutableLiveData<List<Activity>>()

    // Requisitos para obtener los datos
    private val getRodadasRequirement = GetRodadasRequirement()
    private val getEventosRequirement = GetEventosRequirement()
    private val getTalleresRequirement = GetTalleresRequirement()

    private val postJoinActivity = PostJoinActivity()

    // Función para cargar rodadas
    fun getRodadas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getRodadasRequirement()
                Log.d("ActivitiesViewModel", "Rodadas result: $result")
                rodadasLiveData.postValue(result)
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error obteniendo rodadas", e)
                rodadasLiveData.postValue(emptyList())
            }
        }
    }

    // Función para cargar eventos
    fun getEventos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getEventosRequirement()
                Log.d("ActivitiesViewModel", "Eventos result: $result")
                eventosLiveData.postValue(result)
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error obteniendo eventos", e)
                eventosLiveData.postValue(emptyList())
            }
        }
    }

    // Función para cargar talleres
    fun getTalleres() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getTalleresRequirement()
                Log.d("ActivitiesViewModel", "Talleres result: $result")
                talleresLiveData.postValue(result)
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error obteniendo talleres", e)
                talleresLiveData.postValue(emptyList())
            }
        }
    }


    // Función para inscribir al usuario en una actividad
    fun postInscribirActividad(actividadId: String, tipo: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try{
                Log.d("ActivitiesViewModel", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
                postJoinActivity(actividadId, tipo)
                Log.d("postInscribirActivityviewModel", "btnJoin clicked. Activity ID: $actividadId, Type: $tipo")
            }
            catch (e: Exception){
                Log.e("postInscribirActivityviewModel", "Error al inscribir actividad", e)
            }

        }
    }


}