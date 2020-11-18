package com.bdjobs.app.liveInterview.ui.interview_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

class LiveInterviewListViewModelFactory (
        private val repository: LiveInterviewRepository,
        val activity : String

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LiveInterviewListViewModel(repository,activity) as T
    }


}