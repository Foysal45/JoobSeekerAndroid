package com.bdjobs.app.resume_dashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository

//
// Created by Soumik on 6/20/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@Suppress("UNCHECKED_CAST")
class DashboardViewModelFactory(
    private val repository: ResumeDashboardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository) as T
    }

}