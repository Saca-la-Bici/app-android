package com.kotlin.sacalabici.framework.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.domain.activities.GetEventosRequirement
import com.kotlin.sacalabici.domain.activities.GetRodadasRequirement
import com.kotlin.sacalabici.domain.activities.GetTalleresRequirement
import com.kotlin.sacalabici.domain.activities.PermissionsRequirement
import com.kotlin.sacalabici.domain.activities.PostCancelActivity
import com.kotlin.sacalabici.domain.activities.PostJoinActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivitiesViewModel(): ViewModel() {
    // LiveData para observar los datos de la UI
    val rodadasLiveData = MutableLiveData<List<Activity>>()
    val eventosLiveData = MutableLiveData<List<Activity>>()
    val talleresLiveData = MutableLiveData<List<Activity>>()
    private val _permissionsLiveData = MutableLiveData<List<String>>()
    val permissionsLiveData: LiveData<List<String>> = _permissionsLiveData

    // LiveData para mensajes de error
    val errorMessageLiveData = MutableLiveData<String?>() // Permitir valores nulos
    val emptyListActs = "Aún no hay datos para mostrar"
    val errorDB = "Error al obtener los datos"

    // Requisitos para obtener los datos
    private val getRodadasRequirement = GetRodadasRequirement()
    private val getEventosRequirement = GetEventosRequirement()
    private val getTalleresRequirement = GetTalleresRequirement()
    private val permissionsRequirement = PermissionsRequirement()

    init {
        getPermissions()
    }

    private val postJoinActivity = PostJoinActivity()
    private val postCancelActivity = PostCancelActivity()

    // Función para cargar rodadas
    fun getRodadas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getRodadasRequirement()
                Log.d("ActivitiesViewModel", "Rodadas result: $result")
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

    fun postCancelarInscripcion(actividadId: String, tipo: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try{
                Log.d("ActivitiesViewModel", "btnJoin for cancel clicked. Activity ID: $actividadId, Type: $tipo")
                postCancelActivity(actividadId, tipo)
                Log.d("postCancelarActivityviewModel", "btnJoin for cancel clicked. Activity ID: $actividadId, Type: $tipo")
            }
            catch (e: Exception){
                Log.e("postCancelarActivityviewModel", "Error al cancelar actividad", e)
            }

        }
    }


}