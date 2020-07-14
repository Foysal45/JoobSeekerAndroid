package com.bdjobs.app.videoInterview.util

import android.app.Application
import androidx.fragment.app.Fragment
import com.bdjobs.app.BackgroundJob.App
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.payment.PaymentViewModelFactory
import com.bdjobs.app.sms.ui.payment_success.PaymentSuccessViewModelFactory
import com.bdjobs.app.sms.ui.settings.SettingsViewModelFactory
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.transaction.ui.TransactionListModelFactory
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModelFactory
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModelFactory
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListModelFactory
import com.bdjobs.app.videoInterview.ui.record_video.RecordVideoViewModelFactory

object ViewModelFactoryUtil {

    //-----Video Interview-----/

    fun provideVideoInterviewQuestionListViewModelFactory(fragment: Fragment, jobId: String?, applyId : String?): QuestionListViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return QuestionListViewModelFactory(repository,jobId,applyId)
    }

    fun provideVideoInterviewRecordVideoViewModelFactory(fragment: Fragment): RecordVideoViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return RecordVideoViewModelFactory(repository)
    }

    fun provideVideoInterviewInvitationDetailsViewModelFactory(fragment: Fragment, jobId: String?): VideoInterviewDetailsViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return VideoInterviewDetailsViewModelFactory(repository,jobId)
    }

    fun provideVideoInterviewListModelFactory(fragment: Fragment) : VideoInterviewListModelFactory{
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return VideoInterviewListModelFactory(repository)
    }

    //-----Video Interview-----/



    //-----SMS Package-----//

    fun provideSMSPaymentViewModelFactory(fragment: Fragment, totalSMS : Int, totalTaka : Int) : PaymentViewModelFactory{
        val repository = SMSRepository(fragment.requireContext().applicationContext as Application)
        return PaymentViewModelFactory(repository,totalSMS,totalTaka)
    }

    fun provideSMSPaymentSuccessViewModelFactory(fragment: Fragment) : PaymentSuccessViewModelFactory{
        val repository = SMSRepository(fragment.requireContext().applicationContext as Application)
        return PaymentSuccessViewModelFactory(repository)
    }

    fun provideSMSSettingsViewModelFactory(fragment: Fragment) : SettingsViewModelFactory{
        val repository = SMSRepository(fragment.requireContext().applicationContext as Application)
        return SettingsViewModelFactory(repository)
    }

    //-----SMS Package-----//




    //-------Transaction List-------//

    fun provideTransactionListViewModelFactory(fragment: Fragment) : TransactionListModelFactory{
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return TransactionListModelFactory(repository)
    }

}