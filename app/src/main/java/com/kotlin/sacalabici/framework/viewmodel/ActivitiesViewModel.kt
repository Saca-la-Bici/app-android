package com.kotlin.sacalabici.framework.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.data.models.activities.LocationR
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.Route
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
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Rodada
import com.kotlin.sacalabici.domain.activities.PostActivityRequirement
import com.kotlin.sacalabici.data.network.model.ActivityInfo
import com.kotlin.sacalabici.domain.activities.PostCancelActivity
import com.kotlin.sacalabici.domain.activities.PostJoinActivity
import kotlinx.coroutines.launch
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
    val selectedActivityLiveData = MutableLiveData<Activity?>()

    // LiveData para mensajes de error
    val errorMessageLiveData = MutableLiveData<String?>() // Permitir valores nulos
    val emptyListActs = "Aún no hay datos para mostrar"
    val errorDB = "Error al obtener los datos"

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
                if (result == null) {
                    errorMessageLiveData.postValue("Actividad no encontrada")
                } else {
                    errorMessageLiveData.postValue(null)
                }
                selectedActivityLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue("Error al obtener la actividad")
                selectedActivityLiveData.postValue(null)
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

    fun getRodadaInfo(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val rodadaInfoBase = getRodadaInfoRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
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
                val routeInfoBase = routeRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
                _routeInfoLiveData.postValue(routeInfoBase) // Actualiza el valor del LiveData
            } catch (e: Exception) {
                // Maneja el error, podrías enviar un mensaje de error a otro LiveData si es necesario
            }
        }
    }

    fun getLocation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locationInfoBase = getUbicacionRequirement(id) // Supongo que este método devuelve un RodadaInfoBase
                _locationInfoLiveData.postValue(locationInfoBase) // Actualiza el valor del LiveData
            } catch (e: Exception) {
                // Maneja el error, podrías enviar un mensaje de error a otro LiveData si es necesario
            }
        }
    }

    // Función para inscribir al usuario en una actividad
    fun postInscribirActividad(actividadId: String, tipo: String, callback: (Boolean, String) -> Unit) {
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


    fun postCancelarInscripcion(actividadId: String, tipo: String, callback: (Boolean, String) -> Unit) {
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
}