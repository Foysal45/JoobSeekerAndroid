package com.bdjobs.app.sms.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.payment.PaymentViewModel

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
        private val repository: SMSRepository

): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }
}

