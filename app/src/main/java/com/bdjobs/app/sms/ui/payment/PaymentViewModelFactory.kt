package com.bdjobs.app.sms.ui.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel

@Suppress("UNCHECKED_CAST")
class PaymentViewModelFactory(
        private val repository: SMSRepository,
        private val totalSMS : Int?,
        private val totalTaka : Int?,
        private val fragment: PaymentFragment
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PaymentViewModel(repository,totalSMS,totalTaka,fragment) as T
    }
}
