package com.bdjobs.app.videoResume.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository

@Suppress("UNCHECKED_CAST")
class VideoResumeLandingViewModelFactory(
        private val repository: VideoResumeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoResumeLandingViewModel(repository) as T
    }
}