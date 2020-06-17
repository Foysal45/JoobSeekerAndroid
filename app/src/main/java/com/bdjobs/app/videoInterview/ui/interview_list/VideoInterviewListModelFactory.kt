package com.bdjobs.app.videoInterview.ui.interview_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

class VideoInterviewListModelFactory(
        private val repository: VideoInterviewRepository

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoInterviewListViewModel(repository) as T
    }


}