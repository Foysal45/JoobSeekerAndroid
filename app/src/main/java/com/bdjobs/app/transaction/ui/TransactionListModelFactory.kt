package com.bdjobs.app.transaction.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListViewModel

class TransactionListModelFactory( private val repository: TransactionRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransactionListViewModel(repository) as T
    }


}