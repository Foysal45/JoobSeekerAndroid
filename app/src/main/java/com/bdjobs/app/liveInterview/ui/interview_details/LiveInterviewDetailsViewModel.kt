package com.bdjobs.app.liveInterview.ui.interview_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class LiveInterviewDetailsViewModel(private val repository: LiveInterviewRepository, val jobId : String) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _liveInterviewDetailsData = MutableLiveData<List<LiveInterviewDetails.Data?>>()
    val liveInterviewDetailsData: LiveData<List<LiveInterviewDetails.Data?>> = _liveInterviewDetailsData

    private val _commonData = MutableLiveData<LiveInterviewDetails.Common?>()
    val commonData: LiveData<LiveInterviewDetails.Common?> = _commonData

    init {
        getLiveInterviewDetails()
    }

    private fun getLiveInterviewDetails() {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLiveInterviewDetailsFromRemote(jobId)
                _liveInterviewDetailsData.value = response.data
                _commonData.value = response.common
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onYesButtonClick(){

    }

    fun onNoButtonClick(){

    }

    fun onRescheduleButtonClick(){

    }

}