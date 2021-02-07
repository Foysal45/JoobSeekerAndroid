package com.bdjobs.app.videoResume.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository

@Suppress("UNCHECKED_CAST")
class VideoResumeLandingViewModelFactory(
        private val repository: VideoResumeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoResumeLandingViewModel(repository) as T
    }
}