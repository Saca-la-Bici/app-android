package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.domain.activities.PostActivityRequirement
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.network.model.ActivityInfo
import com.kotlin.sacalabici.data.network.model.Informacion
import com.kotlin.sacalabici.domain.activities.GetEventosRequirement
import com.kotlin.sacalabici.domain.activities.GetRodadasRequirement
import com.kotlin.sacalabici.domain.activities.GetTalleresRequirement
import com.kotlin.sacalabici.domain.activities.PermissionsRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitiesViewModel(): ViewModel() {
    // LiveData para observar los datos de la UI
    val rodadasLiveData = MutableLiveData<List<Activity>>()
    val eventosLiveData = MutableLiveData<List<Activity>>()
    val talleresLiveData = MutableLiveData<List<Activity>>()
    private val _permissionsLiveData = MutableLiveData<List<String>>()
    val permissionsLiveData: LiveData<List<String>> = _permissionsLiveData
    val activityInfo = MutableLiveData<ActivityInfo>()

    // LiveData para mensajes de error
    val errorMessageLiveData = MutableLiveData<String?>() // Permitir valores nulos
    val emptyListActs = "Aún no hay datos para mostrar"
    val errorDB = "Error al obtener los datos"

    // Requisitos para obtener los datos
    private val getRodadasRequirement = GetRodadasRequirement()
    private val getEventosRequirement = GetEventosRequirement()
    private val getTalleresRequirement = GetTalleresRequirement()
    private val requirement = PostActivityRequirement()
    private val permissionsRequirement = PermissionsRequirement()

    init {
        getPermissions()
    }

    // Función para cargar rodadas
    fun getRodadas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getRodadasRequirement()
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListActs)
                } else {
                    errorMessageLiveData.postValue(null) // Limpiar mensaje de error
                }
                rodadasLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
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
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListActs)
                }
                else {
                    errorMessageLiveData.postValue(null) // Limpiar mensaje de error
                }
                eventosLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
                eventosLiveData.postValue(emptyList())
            }
        }
    }

    // Función para cargar talleres
    fun getTalleres() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getTalleresRequirement()
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListActs)
                } else {
                    errorMessageLiveData.postValue(null) // Limpiar mensaje de error
                }
                talleresLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
                talleresLiveData.postValue(emptyList())
            }
        }
    }

    // Función para registrar un evento
    fun postActivityEvento(evento: ActivityModel) {
        viewModelScope.launch {
            try {
                Log.d("ActivitiesViewModel", "Registrando evento: $evento")
                requirement.postActivityEvento(evento)
                Log.d("ActivitiesViewModel", "Evento registrado exitosamente")
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error al registrar evento", e)
            }
        }
    }

    // Función para registrar una rodada
    fun postActivityRodada(rodada: Rodada) {
        viewModelScope.launch {
            try {
                Log.d("ActivitiesViewModel", "Registrando rodada: $rodada")
                requirement.postActivityRodada(rodada)
                Log.d("ActivitiesViewModel", "Rodada registrada exitosamente")
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error al registrar rodada", e)
            }
        }
    }

    // Función para registrar un taller
    fun postActivityTaller(taller: ActivityModel) {
        viewModelScope.launch {
            try {
                Log.d("ActivitiesViewModel", "Registrando taller: $taller")
                requirement.postActivityTaller(taller)
                Log.d("ActivitiesViewModel", "Taller registrada exitosamente")
            } catch (e: Exception) {
                Log.e("ActivitiesViewModel", "Error al registrar taller", e)
            }
        }
    }

    fun getPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = permissionsRequirement.getPermissions()
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListActs)
                } else {
                    _permissionsLiveData.postValue(result)
                }
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
            }
        }
    }
}