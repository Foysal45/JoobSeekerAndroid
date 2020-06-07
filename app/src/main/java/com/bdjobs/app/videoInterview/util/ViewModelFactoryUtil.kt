package com.bdjobs.app.videoInterview.util

import android.app.Application
import androidx.fragment.app.Fragment
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModelFactory
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModelFactory
import com.bdjobs.app.videoInterview.ui.record_video.RecordVideoViewModelFactory

object ViewModelFactoryUtil {

    fun provideVideoInterviewQuestionDetailsViewModelFactory(fragment: Fragment): QuestionDetailsViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return QuestionDetailsViewModelFactory(repository)
    }

    fun provideVideoInterviewRecordVideoViewModelFactory(fragment: Fragment): RecordVideoViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return RecordVideoViewModelFactory(repository)
    }

    fun provideVideoInterviewInvitationDetailsViewModelFactory(fragment: Fragment, jobId: String?): VideoInterviewDetailsViewModelFactory {
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return VideoInterviewDetailsViewModelFactory(repository,jobId)
    }
}