package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.profile.ProfileBase
import com.kotlin.sacalabici.data.models.profile.Profile
import com.kotlin.sacalabici.domain.GetProfileRequirement
import com.kotlin.sacalabici.domain.profile.PatchProfileRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profileObjectLiveData = MutableLiveData<ProfileBase?>()
    val profileObjectLiveData: MutableLiveData<ProfileBase?> = _profileObjectLiveData

    private val getProfileRequirement = GetProfileRequirement()
    private val patchProfileRequirement = PatchProfileRequirement()

    fun getProfile(): MutableLiveData<ProfileBase?> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: ProfileBase? = getProfileRequirement()
                if (result != null) {
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
                KmCompleted = 0,
                TimeCompleted = 0,
                activitiesCompleted = 0,
                fireUID = "",
                emergencyNumber = "",
                date = "",
                url = 0,
                pImage =""
            )
        )
    }

    fun patchProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                patchProfileRequirement(profile)
            } catch (e: Exception) {
                throw e
            }
        }
    }

}