package com.bdjobs.app.sms.ui.payment_success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.payment.PaymentViewModel

@Suppress("UNCHECKED_CAST")
class PaymentSuccessViewModelFactory(
        private val repository: SMSRepository

): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PaymentSuccessViewModel(repository) as T
    }
}

