package com.bdjobs.app.sms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.payment_success.PaymentSuccessViewModel

@Suppress("UNCHECKED_CAST")
class HomeCommonViewModelFactory(
        private val repository: SMSRepository

): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeCommonViewModel(repository) as T
    }
}
