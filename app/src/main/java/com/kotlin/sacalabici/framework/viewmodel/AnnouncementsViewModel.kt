package com.kotlin.sacalabici.framework.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementObjectBase
import com.kotlin.sacalabici.domain.announcement.AnnouncementListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.domain.announcement.DeleteAnnouncementRequirement
import com.kotlin.sacalabici.domain.announcement.PostAnnouncementRequirement
import com.kotlin.sacalabici.domain.announcement.PatchAnnouncementRequirement

class AnnouncementsViewModel: ViewModel() {
    val announcementObjectLiveData = MutableLiveData<List<AnnouncementBase>>()
    val permissionsLiveData = MutableLiveData<List<String>>()
    private val announcementListRequirement = AnnouncementListRequirement()
    private val postAnnouncementRequirement = PostAnnouncementRequirement()
    private val deleteAnnouncementRequirement = DeleteAnnouncementRequirement()
    private val patchAnnouncementRequirement = PatchAnnouncementRequirement()

    fun getAnnouncementList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: AnnouncementObjectBase? = announcementListRequirement()
                val reversedResult = result!!.announcements.reversed()
                announcementObjectLiveData.postValue(reversedResult)
                permissionsLiveData.postValue(result.permissions)
            } catch (e: Exception) {
                announcementObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun deleteAnnouncement(id: String, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                deleteAnnouncementRequirement(id)
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }


    fun postAnnouncement(announcement: Announcement, context: Context, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                postAnnouncementRequirement(announcement, context)
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun patchAnnouncement(id: String, announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                patchAnnouncementRequirement(id, announcement)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}