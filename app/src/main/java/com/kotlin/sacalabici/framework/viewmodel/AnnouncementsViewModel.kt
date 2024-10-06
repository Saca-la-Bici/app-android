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

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteAnnouncementRequirement(id)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun postAnnouncement(announcement: Announcement, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postAnnouncementRequirement(announcement, context)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun patchAnnouncement(id: String, announcement: Announcement, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                patchAnnouncementRequirement(id, announcement, context)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}