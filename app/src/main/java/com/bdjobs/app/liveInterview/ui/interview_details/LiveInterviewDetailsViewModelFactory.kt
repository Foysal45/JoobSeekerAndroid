package com.bdjobs.app.liveInterview.ui.interview_details

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

@Suppress("UNCHECKED_CAST")
class LiveInterviewDetailsViewModelFactory (
        private val repository: LiveInterviewRepository,
        private val contentResolver: ContentResolver,
        private val jobId : String

) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LiveInterviewDetailsViewModel(repository,contentResolver,jobId) as T
    }


}