package com.bdjobs.app.videoResume.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModel


@Suppress("UNCHECKED_CAST")
class VideoResumeRecordVideoViewModelFactory(
        private val repository: VideoResumeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecordVideoResumeViewModel(repository) as T
    }
}