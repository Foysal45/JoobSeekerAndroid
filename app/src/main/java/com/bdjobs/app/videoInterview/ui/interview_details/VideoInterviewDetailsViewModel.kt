package com.bdjobs.app.videoInterview.ui.interview_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class VideoInterviewDetailsViewModel(private val repository: VideoInterviewRepository, val jobId : String?) : ViewModel() {

    private val _detailsData = MutableLiveData<VideoInterviewDetails.Data?>()
    val detailsData : LiveData<VideoInterviewDetails.Data?> = _detailsData

    private val _commonData = MutableLiveData<VideoInterviewDetails.Common?>()
    val commonData : LiveData<VideoInterviewDetails.Common?> = _commonData

    init {
        getVideoInterviewDetails()
    }

     private fun getVideoInterviewDetails() {
        viewModelScope.launch {
            try {
                val response = repository.getVideoInterviewDetailsFromRemote(jobId)
                _detailsData.value = response.data?.get(0)
                _commonData.value = response.common
            } catch (e:Exception){

            }
        }
    }
}