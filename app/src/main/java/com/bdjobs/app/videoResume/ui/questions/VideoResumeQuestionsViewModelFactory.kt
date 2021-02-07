package com.bdjobs.app.videoResume.ui.questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository

@Suppress("UNCHECKED_CAST")
class VideoResumeQuestionsViewModelFactory(
        private val repository: VideoResumeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoResumeQuestionsViewModel(repository) as T
    }
}