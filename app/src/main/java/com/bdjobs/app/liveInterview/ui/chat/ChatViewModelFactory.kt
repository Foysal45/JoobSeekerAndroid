package com.bdjobs.app.liveInterview.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository

//
// Created by Soumik on 5/18/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory(
        private val repository: LiveInterviewRepository,
        private val processID:String?
) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(repository, processID) as T
    }
}