package com.kotlin.sacalabici.framework.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.domain.activities.GetActivityByIdRequirement
import com.kotlin.sacalabici.domain.activities.GetEventosRequirement
import com.kotlin.sacalabici.domain.activities.GetRodadasRequirement
import com.kotlin.sacalabici.domain.activities.GetTalleresRequirement
import com.kotlin.sacalabici.domain.activities.PermissionsRequirement
import com.kotlin.sacalabici.domain.activities.PostLocationRequirement
import com.kotlin.sacalabici.domain.activities.RodadaInfoRequirement
import com.kotlin.sacalabici.domain.activities.UbicacionRequirement
import com.kotlin.sacalabici.domain.routes.RouteRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.domain.activities.PostActivityRequirement
import com.kotlin.sacalabici.domain.activities.PostValidateAttendance
import com.kotlin.sacalabici.data.network.model.ActivityInfo
import com.kotlin.sacalabici.domain.activities.DeleteLocationRequirement
import com.kotlin.sacalabici.domain.activities.PostCancelActivity
import com.kotlin.sacalabici.domain.activities.PostJoinActivity
import com.kotlin.sacalabici.utils.SingleLiveEvent
import kotlinx.coroutines.withContext

class ActivitiesViewModel(): ViewModel() {
    // LiveData para observar los datos de la UI
    val rodadasLiveData = MutableLiveData<List<Activity>>()
    val _rodadaInfoLiveData = MutableLiveData<RodadaInfoBase?>()
    val rodadaInfo: MutableLiveData<RodadaInfoBase?> get() = _rodadaInfoLiveData
    val _routeInfoLiveData = MutableLiveData<RouteInfoObjectBase?>()
    val routeInfo: MutableLiveData<RouteInfoObjectBase?> get() = _routeInfoLiveData
    val _locationInfoLiveData = MutableLiveData<List<LocationR>?>()
    val locationInfo: MutableLiveData<List<LocationR>?> get() = _locationInfoLiveData
    val eventosLiveData = MutableLiveData<List<Activity>>()
    val talleresLiveData = MutableLiveData<List<Activity>>()
    private val _permissionsLiveData = MutableLiveData<List<String>>()
    val permissionsLiveData: LiveData<List<String>> = _permissionsLiveData
    val activityInfo = MutableLiveData<ActivityInfo>()

    // LiveData para observar una actividad por ID
    val selectedActivityLiveData = SingleLiveEvent<Activity?>()

    // Requisitos para obtener los datos
    private val getRodadasRequirement = GetRodadasRequirement()
    private val getEventosRequirement = GetEventosRequirement()
    private val getTalleresRequirement = GetTalleresRequirement()
    private val requirement = PostActivityRequirement()
    private val getActivityByIdRequirement = GetActivityByIdRequirement()
    private val permissionsRequirement = PermissionsRequirement()
    private val getRodadaInfoRequirement = RodadaInfoRequirement()
    private val postLocationRequirement = PostLocationRequirement()
    private val routeRequirement = RouteRequirement()
    private val getUbicacionRequirement = UbicacionRequirement()
    private val deleteUbicacionRequirement = DeleteLocationRequirement()

    init {
        getPermissions()
    }

    private val postJoinActivity = PostJoinActivity()
    private val postCancelActivity = PostCancelActivity()
    private val postValidateAttendance= PostValidateAttendance()

    // Función para cargar rodadas
    fun getRodadas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getRodadasRequirement()
                rodadasLiveData.postValue(result)
            } catch (e: Exception) {
                rodadasLiveData.postValue(emptyList())
            }
        }
    }

    // Función para cargar eventos
    fun getEventos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getEventosRequirement()
                eventosLiveData.postValue(result)
            } catch (e: Exception) {
                eventosLiveData.postValue(emptyList())
            }
        }
    }

    // Función para cargar talleres
    fun getTalleres() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getTalleresRequirement()
                talleresLiveData.postValue(result)
            } catch (e: Exception) {
                talleresLiveData.postValue(emptyList())
            }
        }
    }

    // Función para registrar un evento
    fun postActivityEvento(evento: ActivityModel, context: Context) {
        viewModelScope.launch {
            try {
                requirement.postActivityEvento(evento, context)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Función para registrar una rodada
    fun postActivityRodada(rodada: Rodada, context: Context) {
        viewModelScope.launch {
            try {
                requirement.postActivityRodada(rodada, context)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Función para registrar un taller
    fun postActivityTaller(taller: ActivityModel, context: Context) {
        viewModelScope.launch {
            try {
                requirement.postActivityTaller(taller, context)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getActivityById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getActivityByIdRequirement(id)
                selectedActivityLiveData.postValue(result)
            } catch (e: Exception) {
                selectedActivityLiveData.postValue(null)
            }
        }
    }

    fun getPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = permissionsRequirement.getPermissions()
                _permissionsLiveData.postValue(result)
            } catch (e: Exception) {
                _permissionsLiveData.postValue(emptyList())
            }
        }
    }

    fun getRodadaInfo(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val rodadaInfoBase =
                    getRodadaInfoRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
                _rodadaInfoLiveData.postValue(rodadaInfoBase) // Actualiza el valor del LiveData
            } catch (e: Exception) {
                // Maneja el error, podrías enviar un mensaje de error a otro LiveData si es necesario
            }
        }
    }

    fun postUbicacion(id: String, loca: LocationR) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postLocationRequirement(id, loca)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun getRoute(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val routeInfoBase =
                    routeRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
                _routeInfoLiveData.postValue(routeInfoBase) // Actualiza el valor del LiveData
            } catch (e: Exception) {
                // Maneja el error, podrías enviar un mensaje de error a otro LiveData si es necesario
            }
        }
    }

    fun getLocation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locationInfoBase =
                    getUbicacionRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
                _locationInfoLiveData.postValue(locationInfoBase) // Actualiza el valor del LiveData
            } catch (e: Exception) {
                // Maneja el error, podrías enviar un mensaje de error a otro LiveData si es necesario
            }
        }
    }

    // Función para inscribir al usuario en una actividad
    fun postInscribirActividad(
        actividadId: String,
        tipo: String,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (success, message) = postJoinActivity(actividadId, tipo)
                withContext(Dispatchers.Main) {
                    callback(success, message)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(false, "Error desconocido. Por favor, intenta más tarde.")
                }
            }
        }
    }


    fun postCancelarInscripcion(
        actividadId: String,
        tipo: String,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (success, message) = postCancelActivity(actividadId, tipo)
                withContext(Dispatchers.Main) {
                    callback(success, message)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(false, "Error desconocido. Por favor, intenta más tarde.")
                }
            }
        }
    }

    fun validateAttendance(
        IDRodada: String,
        codigo: Int,
        callback: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (success, message) = postValidateAttendance(IDRodada, codigo)

                withContext(Dispatchers.Main) {
                    callback(success, message)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(false, "Error desconocido. Por favor, intenta más tarde.")
                }
            }
        }
    }

    fun deleteUbicacion(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteUbicacionRequirement(id)
            } catch (e: Exception) {
                throw e
            }
        }
    }

}