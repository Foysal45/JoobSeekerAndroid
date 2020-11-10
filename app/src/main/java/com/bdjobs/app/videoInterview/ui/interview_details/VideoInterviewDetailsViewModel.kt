package com.bdjobs.app.videoInterview.ui.interview_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch

class VideoInterviewDetailsViewModel(private val repository: VideoInterviewRepository, val jobID: String?) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _detailsData = MutableLiveData<VideoInterviewDetails.Data?>()
    val detailsData: LiveData<VideoInterviewDetails.Data?> = _detailsData

    private val _commonData = MutableLiveData<VideoInterviewDetails.Common?>()
    val commonData: LiveData<VideoInterviewDetails.Common?> = _commonData

    private val _displayQuestionListEvent = MutableLiveData<Event<Boolean>>()
    val displayQuestionListEvent: LiveData<Event<Boolean>> = _displayQuestionListEvent

    private val _displayGuidelineEvent = MutableLiveData<Event<Boolean>>()
    val displayGuidelineEvent: LiveData<Event<Boolean>> = _displayGuidelineEvent

    private val _jobId = MutableLiveData<String>().apply {
        value = jobID
    }
    val jobId: LiveData<String> = _jobId

    private val _applyId = MutableLiveData<String>()
    val applyId: LiveData<String> = _applyId

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String> = _jobTitle

//    init {
//        getVideoInterviewDetails()
//    }

    fun getVideoInterviewDetails() {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getVideoInterviewDetailsFromRemote(jobID)
                _detailsData.value = response.data?.get(0)
                _commonData.value = response.common
                _applyId.value = response.common?.applyId
                _jobId.value = response.common?.jobId
                _jobTitle.value = response.common?.jobTitle
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onViewButtonClick() {
        _displayQuestionListEvent.value = Event(true)
    }

    fun onStartButtonClick() {
        //_displayQuestionListEvent.value = Event(true)
        _displayGuidelineEvent.value = Event(true)
    }
}