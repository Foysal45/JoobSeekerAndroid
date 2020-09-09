package com.bdjobs.app.liveInterview.ui.interview_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

class LiveInterviewListViewModelFactory (
        private val repository: LiveInterviewRepository

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LiveInterviewListViewModel(repository) as T
    }


}