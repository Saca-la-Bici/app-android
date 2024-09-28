package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ActivityModel
import com.kotlin.sacalabici.data.network.model.Informacion
import com.kotlin.sacalabici.data.network.model.Rodada
import kotlinx.coroutines.launch
import com.kotlin.sacalabici.domain.PostActivityRequirement

class ActivitiesViewModel: ViewModel() {
    private val requirement = PostActivityRequirement()

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
}