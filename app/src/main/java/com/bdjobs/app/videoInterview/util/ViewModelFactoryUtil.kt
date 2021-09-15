package com.bdjobs.app.videoInterview.util

import android.app.Application
import androidx.fragment.app.Fragment
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.home.HomeViewModelFactory
import com.bdjobs.app.sms.ui.payment.PaymentFragment
import com.bdjobs.app.sms.ui.payment.PaymentViewModelFactory
import com.bdjobs.app.sms.ui.payment_success.PaymentSuccessViewModelFactory
import com.bdjobs.app.sms.ui.settings.SettingsViewModelFactory
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.transaction.ui.TransactionFilterModelFactory
import com.bdjobs.app.transaction.ui.TransactionListModelFactory
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModelFactory
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModelFactory
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListModelFactory
import com.bdjobs.app.videoInterview.ui.record_video.RecordVideoViewModelFactory
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import com.bdjobs.app.videoResume.ui.home.VideoResumeLandingViewModelFactory
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModelFactory
import com.bdjobs.app.videoResume.ui.record.VideoResumeRecordVideoViewModelFactory
import com.bdjobs.app.videoResume.ui.view.VideoResumeViewVideoViewModelFactory

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

    fun provideSMSHomeViewModelFactory(fragment: Fragment) : HomeViewModelFactory{
        val repository = SMSRepository(fragment.requireContext().applicationContext as Application)
        return HomeViewModelFactory(repository)
    }

    fun provideSMSPaymentViewModelFactory(fragment: Fragment, totalSMS : Int, totalTaka : Int, isFree : String?, bonusSMS:Int?) : PaymentViewModelFactory{
        val repository = SMSRepository(fragment.requireContext().applicationContext as Application)
        return PaymentViewModelFactory(repository,totalSMS,totalTaka,isFree,fragment as PaymentFragment,bonusSMS)
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
        val repository = TransactionRepository(fragment.requireContext().applicationContext as Application)
        return TransactionListModelFactory(repository)
    }

    fun provideTransactionFilterViewModelFactory(fragment: Fragment) : TransactionFilterModelFactory {
        var application = fragment.requireContext().applicationContext as Application
        return TransactionFilterModelFactory(application)
    }

    //-------Video Resume-------//

    fun provideVideoResumeLandingViewModelFactory(fragment: Fragment) : VideoResumeLandingViewModelFactory {
        val repository = VideoResumeRepository(fragment.requireContext().applicationContext as Application)
        return VideoResumeLandingViewModelFactory(repository)
    }

    fun provideVideoResumeQuestionsViewModelFactory(fragment: Fragment) : VideoResumeQuestionsViewModelFactory {
        val repository = VideoResumeRepository(fragment.requireContext().applicationContext as Application)
        return VideoResumeQuestionsViewModelFactory(repository)
    }

    fun provideVideoResumeRecordVideoViewModelFactory(fragment: Fragment) : VideoResumeRecordVideoViewModelFactory {
        val repository = VideoResumeRepository(fragment.requireContext().applicationContext as Application)
        return VideoResumeRecordVideoViewModelFactory(repository)
    }

    fun provideVideoResumeViewVideoViewModelFactory(fragment: Fragment) : VideoResumeViewVideoViewModelFactory {
        val repository = VideoResumeRepository(fragment.requireContext().applicationContext as Application)
        return VideoResumeViewVideoViewModelFactory(repository)
    }

    //-------Video Resume-------//


}