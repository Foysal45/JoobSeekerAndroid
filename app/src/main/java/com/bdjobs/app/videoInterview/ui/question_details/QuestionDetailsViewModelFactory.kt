package com.bdjobs.app.videoInterview.ui.question_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

@Suppress("UNCHECKED_CAST")
class QuestionDetailsViewModelFactory(
        private val repository: VideoInterviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuestionDetailsViewModel(repository) as T
    }
}