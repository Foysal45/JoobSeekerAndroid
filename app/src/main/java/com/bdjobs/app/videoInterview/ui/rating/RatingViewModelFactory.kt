package com.bdjobs.app.videoInterview.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

@Suppress("UNCHECKED_CAST")
class RatingViewModelFactory (
        private val repository: VideoInterviewRepository,
        val applyId: String,
        val jobId: String

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RatingViewModel(repository,applyId,jobId) as T
    }

}