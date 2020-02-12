package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.assessment.models.ScheduleData

class BookingOverviewViewModelFactory (
        private val scheduleData: ScheduleData,
        private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingOverviewViewModel::class.java)) {
            return BookingOverviewViewModel(scheduleData, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
