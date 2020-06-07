package com.bdjobs.app.videoInterview.ui.interview_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails.VideoInterviewDetailsData
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class VideoInterviewDetailsViewModel(private val repository: VideoInterviewRepository, val jobId : String?) : ViewModel() {

    private val _detailsData = MutableLiveData<VideoInterviewDetailsData?>()

    suspend fun getVideoDetails() {
        viewModelScope.launch {
            try {
                val response = repository.getVideoInterviewDetailsFromRemote(jobId)
                _detailsData.value = response.data?.get(0)
            } catch (e:Exception){

            }
        }
    }
}