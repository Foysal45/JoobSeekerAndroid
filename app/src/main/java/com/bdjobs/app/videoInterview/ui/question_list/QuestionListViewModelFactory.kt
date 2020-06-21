package com.bdjobs.app.videoInterview.ui.question_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

@Suppress("UNCHECKED_CAST")
class QuestionListViewModelFactory(
        private val repository: VideoInterviewRepository,
        private val jobId : String?,
        private val applyId : String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuestionListViewModel(repository,jobId,applyId) as T
    }
}