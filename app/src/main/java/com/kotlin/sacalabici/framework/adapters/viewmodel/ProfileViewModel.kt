package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.ProfileBase
import com.kotlin.sacalabici.domain.GetProfileRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileViewModel: ViewModel() {
    val profileObjectLiveData = MutableLiveData<ProfileBase>()
    private val getProfileRequirement = GetProfileRequirement()

    fun getProfile(userid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result: ProfileBase? = getProfileRequirement(userid)
            result?.let {
                withContext(Dispatchers.Main) {
                    profileObjectLiveData.postValue(it)
                }
            }
        }
    }
}
