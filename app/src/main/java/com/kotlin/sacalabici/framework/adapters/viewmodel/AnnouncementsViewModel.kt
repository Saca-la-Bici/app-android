package com.kotlin.sacalabici.framework.adapters.viewmodel

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
import com.kotlin.sacalabici.domain.PutAnnouncementRequirement
import kotlinx.coroutines.launch

class AnnouncementsViewModel: ViewModel() {
    val announcementObjectLiveData = MutableLiveData<List<AnnouncementBase>>()
    private val announcementListRequirement = AnnouncementListRequirement()
    private val postAnnouncementRequirement = PostAnnouncementRequirement()
    private val deleteAnnouncementRequirement = DeleteAnnouncementRequirement()
    private val putAnnouncementRequirement = PutAnnouncementRequirement()

    // Fetch announcements asynchronously
    fun getAnnouncementList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch announcements from the API (or any source)
                val result: List<AnnouncementBase> = announcementListRequirement()
                // Reverse the result list as needed
                val reversedResult = result.reversed()
                // Post the result to the LiveData, no need to switch to Main explicitly
                announcementObjectLiveData.postValue(reversedResult)
            } catch (e: Exception) {
                Log.e("AnnouncementsViewModel", "Error fetching announcements", e)
                // Handle error, e.g., post an empty list or an error state
                announcementObjectLiveData.postValue(emptyList())
            }
        }
    }

    // Delete announcement
    fun deleteAnnouncement(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = deleteAnnouncementRequirement(id)
            if (result) {
                Log.d("delete", "Announcement deleted")
            } else {
                Log.d("delete", "Announcement not deleted")
            }
        }
    }

    // Post a new announcement
    fun postAnnouncement(announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("AnnouncementsViewModel", "Posting announcement: $announcement")
                postAnnouncementRequirement(announcement)
                Log.d("AnnouncementsViewModel", "Announcement posted successfully")
            } catch (e: Exception) {
                Log.e("AnnouncementsViewModel", "Error posting announcement", e)
            }
        }
    }

    // Update an existing announcement
    fun putAnnouncement(id: String, announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("AnnouncementsViewModel", "Putting announcement: $announcement")
                putAnnouncementRequirement(id, announcement)
                Log.d("AnnouncementsViewModel", "Announcement updated successfully")
            } catch (e: Exception) {
                Log.e("AnnouncementsViewModel", "Error updating announcement", e)
            }
        }
    }
}