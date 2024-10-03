package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.data.models.medals.MedalObjectBase
import com.kotlin.sacalabici.domain.medals.ConsultMedalsListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TotalMedalsViewModel : ViewModel() {
    val medalsObjectLiveData = MutableLiveData<List<MedalBase>>()
    private val medalsListRequirement = ConsultMedalsListRequirement()

    fun getMedalsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: MedalObjectBase? = medalsListRequirement()
                val reversedResult = result!!.medals.reversed()
                medalsObjectLiveData.postValue(reversedResult)
            } catch (e: Exception) {
                e.printStackTrace()
                medalsObjectLiveData.postValue(emptyList())
            }
        }
    }

}