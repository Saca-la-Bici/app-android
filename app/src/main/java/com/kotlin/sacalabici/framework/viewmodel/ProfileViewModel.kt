package com.kotlin.sacalabici.framework.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.activities.ActivityBase
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.domain.profile.GetActivitiesRequirement
import com.kotlin.sacalabici.domain.profile.GetProfileRequirement
import com.kotlin.sacalabici.domain.profile.PatchProfileRequirement
import com.kotlin.sacalabici.domain.profile.DeleteProfileRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val _profileObjectLiveData = MutableLiveData<ProfileBase?>()
    val eventLiveData = MutableLiveData<List<ActivityBase>>()
    val profileObjectLiveData: MutableLiveData<ProfileBase?> = _profileObjectLiveData

    private val getProfileRequirement = GetProfileRequirement()
    private val patchProfileRequirement = PatchProfileRequirement()
    private val getActivitiesRequirement = GetActivitiesRequirement()
    private val deleteProfileRequirement= DeleteProfileRequirement()

    // LiveData para mensajes de error
    val errorMessageLiveData = MutableLiveData<String?>() // Permitir valores nulos
    val emptyListActs = "Aún no hay datos para mostrar"
    val errorDB = "Error al obtener los datos"

    fun getProfile(): MutableLiveData<ProfileBase?> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: ProfileBase? = getProfileRequirement()
                if (result != null) {
                    Log.d("ProfileViewModel", "Kilómetros mensuales recibidos: ${result.kilometrosMes}")
                    _profileObjectLiveData.postValue(result)
                } else {
                    postErrorProfile(result.toString())
                }
            } catch (e: Exception) {
                // Manejo de excepciones de red
                postErrorProfile("error")
            }
        }
        return profileObjectLiveData
    }

    private fun postErrorProfile(error: String) {
        _profileObjectLiveData.postValue(
            ProfileBase(
                id = "",
                user = error,
                name = "",
                birthdate = "",
                bloodtype = "",
                email = "",
                KmCompleted = 0.00,
                TimeCompleted = 0.00,
                activitiesCompleted = 0,
                fireUID = "",
                emergencyNumber = "",
                date = "",
                url = 0,
                pImage ="",
                kilometrosMes = 0,
            )
        )
    }

    suspend fun patchProfile(profile: Profile, context: Context): Boolean { // Manda a llamar la función de patchProfileRequirement con los datos obtenidos en la vista.
        return try {
            withContext(Dispatchers.IO) {
                patchProfileRequirement(profile, context)
            }
            true
        } catch (e: Exception) {
            Log.e("ViewModel", "Error al actualizar el perfil: ${e.message}", e)
            false
        }
    }

    fun deleteProfile( callback: (Result<Unit>) -> Unit){
        viewModelScope.launch {
            try{
                deleteProfileRequirement()
                callback(Result.success(Unit))
            }catch (e: Exception) {
                callback(Result.failure(e))
                Log.e("ViewModel", "Error al eliminar el perfil: ${e.message}", e)
            }
        }
    }

    // Función para cargar eventos
    fun getEventos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getActivitiesRequirement()
                Log.d("Result:", "$result")
                if (result.isEmpty()) {
                    errorMessageLiveData.postValue(emptyListActs)
                }
                else {
                    errorMessageLiveData.postValue(null) // Limpiar mensaje de error
                }
                eventLiveData.postValue(result)
            } catch (e: Exception) {
                errorMessageLiveData.postValue(errorDB)
                eventLiveData.postValue(emptyList())
            }
        }
    }


}