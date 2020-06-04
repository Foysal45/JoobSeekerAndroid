package com.bdjobs.app.videoInterview.util

import android.app.Application
import androidx.fragment.app.Fragment
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModelFactory

object ViewModelFactoryUtil {
    fun provideVideoInterviewQuestionDetailsViewModelFactory(fragment: Fragment) : QuestionDetailsViewModelFactory{
        val repository = VideoInterviewRepository(fragment.requireContext().applicationContext as Application)
        return QuestionDetailsViewModelFactory(repository)
    }
}