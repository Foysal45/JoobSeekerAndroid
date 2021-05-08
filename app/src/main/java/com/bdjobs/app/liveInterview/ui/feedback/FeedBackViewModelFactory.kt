package com.bdjobs.app.liveInterview.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

//
// Created by Soumik on 5/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 BDjobs.com Ltd. All rights reserved.
//

@Suppress("UNCHECKED_CAST")
class FeedBackViewModelFactory(
        private val repository: LiveInterviewRepository,
        val applyId: String?,
        val jobId: String?
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FeedbackViewModel(repository,applyId,jobId) as T
    }
}