package com.bdjobs.app.sms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.sms.data.repository.SMSRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
        private val repository: SMSRepository

): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SMSHomeViewModel(repository) as T
    }
}
