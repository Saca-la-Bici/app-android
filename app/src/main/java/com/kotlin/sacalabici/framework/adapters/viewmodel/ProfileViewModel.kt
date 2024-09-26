package com.kotlin.sacalabici.framework.adapters.viewmodel

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
                    postErrorProfile()
                }
            } catch (e: Exception) {
                // Manejo de excepciones de red
                postErrorProfile()
            }
        }
        return profileObjectLiveData
    }

    private fun postErrorProfile() {
        _profileObjectLiveData.postValue(
            ProfileBase(
                id = "",
                user = "Error",
                name = "",
                birthdate = "",
                bloodtype = "",
                email = "",
                KmCompleted = 0,
                TimeCompleted = 0,
                activitiesCompleted = 0,
                fireUID = "",
                emergencyNumber = "",
                url = 0
            )
        )
    }
}
