package com.bdjobs.app.training.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.training.data.repository.TrainingRepository


@Suppress("UNCHECKED_CAST")
class UpcomingTrainingViewModelFactory(
    private val repository: TrainingRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpcomingTrainingViewModel(repository) as T
    }
}