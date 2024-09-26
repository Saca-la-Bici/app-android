package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.domain.AnnouncementListRequirement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.model.announcement.Announcement
import com.kotlin.sacalabici.domain.DeleteAnnouncementRequirement
import com.kotlin.sacalabici.domain.PostAnnouncementRequirement
import kotlinx.coroutines.launch

class AnnouncementsViewModel: ViewModel() {
    val announcementObjectLiveData = MutableLiveData<List<AnnouncementBase>>()
    private val announcementListRequirement = AnnouncementListRequirement()
    private val postAnnouncementRequirement = PostAnnouncementRequirement()
    private val deleteAnnouncementRequirement = DeleteAnnouncementRequirement()

    fun getAnnouncementList(){
        viewModelScope.launch(Dispatchers.IO) {
            val result: List<AnnouncementBase> = announcementListRequirement()
            val reversedResult = result.reversed()
            CoroutineScope(Dispatchers.Main).launch {
                announcementObjectLiveData.postValue(reversedResult)
            }
        }
    }

    fun deleteAnnouncement(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = deleteAnnouncementRequirement(id)
            if (result){
                Log.d("delete", "Announcement deleted")
            } else {
                Log.d("delete", "Announcement not deleted")
            }
        }
    }

    fun postAnnouncement(announcement: Announcement){
        viewModelScope.launch {
            try {
                Log.d("AnnouncementsViewModel", "Posting announcement: $announcement")
                postAnnouncementRequirement(announcement)
                Log.d("AnnouncementsViewModel", "Announcement posted successfully")
            } catch (e: Exception) {
                Log.e("AnnouncementsViewModel", "Error posting announcement", e)
            }
        }
    }
}