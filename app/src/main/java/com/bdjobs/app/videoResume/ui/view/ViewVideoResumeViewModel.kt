package com.bdjobs.app.videoResume.ui.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoResume.data.models.VideoResumeManager
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import kotlinx.coroutines.launch


class ViewVideoResumeViewModel(private val repository: VideoResumeRepository) : ViewModel() {

    private val _videoResumeManagerData = MutableLiveData<VideoResumeManager?>()
    private val videoResumeManagerData: LiveData<VideoResumeManager?> = _videoResumeManagerData

    private val _onDeleteDoneEvent = MutableLiveData<Event<Boolean>>()
    val onDeleteDoneEvent : LiveData<Event<Boolean>> = _onDeleteDoneEvent

    fun onDeleteResumeButtonClick() {
        deleteSingleVideoOfResume()
    }


    private fun deleteSingleVideoOfResume() {
        viewModelScope.launch {
            try {
                val response = repository.deleteSingleVideoOfResume(videoResumeManagerData.value!!)
                if (response.statuscode == "0" || response.statuscode == 0)
                {
                    _onDeleteDoneEvent.value = Event(true)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun prepareData(videoResumeManager: VideoResumeManager?) {
        _videoResumeManagerData.value = videoResumeManager
    }

}