package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.assessment.models.Booking

class BookingViewModelFactory(
        private val booking: Booking,
        private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(booking, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}