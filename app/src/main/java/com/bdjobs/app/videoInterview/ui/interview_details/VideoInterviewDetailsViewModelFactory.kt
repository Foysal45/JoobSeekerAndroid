package com.bdjobs.app.videoInterview.ui.interview_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModel

@Suppress("UNCHECKED_CAST")
class VideoInterviewDetailsViewModelFactory(
        private val repository: VideoInterviewRepository,
        private val jobId : String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoInterviewDetailsViewModel(repository,jobId) as T
    }
}

