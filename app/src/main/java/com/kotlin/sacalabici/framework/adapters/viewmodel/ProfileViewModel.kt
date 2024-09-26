package com.kotlin.sacalabici.framework.adapters.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ProfileBase
import com.kotlin.sacalabici.domain.GetProfileRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profileObjectLiveData = MutableLiveData<ProfileBase?>()
    val profileObjectLiveData: MutableLiveData<ProfileBase?> = _profileObjectLiveData

    private val getProfileRequirement = GetProfileRequirement()

    fun getProfile(userid: String): MutableLiveData<ProfileBase?> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: ProfileBase? = getProfileRequirement(userid)
                if (result != null) {
                    _profileObjectLiveData.postValue(result)
                } else {
                    postErrorProfile(result.toString())
                }
            } catch (e: Exception) {
                // Manejo de excepciones de red
                postErrorProfile("pilin")
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
                KmCompleted = 0,
                TimeCompleted = 0,
                activitiesCompleted = 0,
                fireUID = "",
                emergencyNumber = "",
                date = "",
                url = 0
            )
        )
    }
}