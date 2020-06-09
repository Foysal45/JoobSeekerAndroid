package com.bdjobs.app.videoInterview.ui.interview_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.videoInterview.data.repository.InterviewListRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModel

class InterviewListViewModelFactory(
        private val repository: InterviewListRepository,
        private val application: Application

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InterviewListViewModel(repository,application) as T
    }


}