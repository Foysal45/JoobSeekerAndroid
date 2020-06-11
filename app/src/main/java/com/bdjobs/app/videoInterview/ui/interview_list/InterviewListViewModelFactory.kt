package com.bdjobs.app.videoInterview.ui.interview_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

class InterviewListViewModelFactory(
        private val repository: VideoInterviewRepository,
        private val application: Application

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InterviewListViewModel(repository,application) as T
    }


}