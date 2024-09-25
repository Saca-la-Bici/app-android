package com.kotlin.sacalabici.framework.adapters.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.domain.AnnouncementListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.domain.DeleteAnnouncementRequirement
import com.kotlin.sacalabici.domain.PostAnnouncementRequirement
import com.kotlin.sacalabici.domain.PutAnnouncementRequirement

class AnnouncementsViewModel: ViewModel() {
    val announcementObjectLiveData = MutableLiveData<List<AnnouncementBase>>()
    private val announcementListRequirement = AnnouncementListRequirement()
    private val postAnnouncementRequirement = PostAnnouncementRequirement()
    private val deleteAnnouncementRequirement = DeleteAnnouncementRequirement()
    private val putAnnouncementRequirement = PutAnnouncementRequirement()

    fun getAnnouncementList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: List<AnnouncementBase> = announcementListRequirement()
                val reversedResult = result.reversed()
                announcementObjectLiveData.postValue(reversedResult)
            } catch (e: Exception) {
                announcementObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = deleteAnnouncementRequirement(id)
        }
    }

    fun postAnnouncement(announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postAnnouncementRequirement(announcement)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun putAnnouncement(id: String, announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                putAnnouncementRequirement(id, announcement)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}