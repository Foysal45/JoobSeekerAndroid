@file:Suppress("UNCHECKED_CAST")

package com.bdjobs.app.liveInterview.ui.interview_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

//
// Created by Soumik on 6/2/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class InterviewSessionViewModelFactory(
        private val repository: LiveInterviewRepository,
        private val processID:String?,
        private val applyID:String?
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InterviewSessionViewModel(
                repository, processID, applyID
        ) as T
    }
}