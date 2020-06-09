package com.bdjobs.app.videoInterview.ui.record_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

@Suppress("UNCHECKED_CAST")
class RecordVideoViewModelFactory(private val repository: VideoInterviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecordVideoViewModel(repository) as T
    }
}